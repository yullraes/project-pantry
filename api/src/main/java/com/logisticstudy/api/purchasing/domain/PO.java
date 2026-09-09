package com.logisticstudy.api.purchasing.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.purchasing.domain.dto.POLineState;
import com.logisticstudy.api.purchasing.domain.vo.OverReceivePolicy;
import com.logisticstudy.api.purchasing.domain.vo.POId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Getter
public class PO {
    public enum Status {
        DRAFT,
        APPROVED, // 얘는 APPROVED 되는 즉시 Supplier쪽에 request 보내야 하는데 이걸 여기서?
        REJECTED, // 공급업체에서 거절
        SENT, // 공급업체에 발주 요청을 보낸 상태
        CANCELLED, // 우리가 취소
        PARTIALLY_RECEIVED, // 부분입고일 경우
        CLOSED // 완전 입고되면 closed, 즉 애초에 물량이 한번에 다 오면 바로 closed
    }
    private final POId id;
    private final SupplierId supplierId;
    private Status status;
    private final String description;
    private final LocalDate orderDate; //발주날짜
    private final LocalDate expectedDeliveryCompleteDate; // 최종 완납 예정일

    private final List<POLine> lines = new ArrayList<>();

    @Builder(access = AccessLevel.PACKAGE)
    private PO(POId id, SupplierId supplierId, Status status, String description, LocalDate orderDate, LocalDate expectedDeliveryCompleteDate) {
        this.id = Require.notNull(id, "발주 ID는 필수입니다.");
        this.supplierId = Require.notNull(supplierId, "공급업체 ID는 필수입니다.");
        this.status = Require.notNull(status, "발주 상태는 필수입니다.");
        this.description = description;
        this.orderDate = Require.notNull(orderDate, "주문 날짜는 필수입니다.");
        this.expectedDeliveryCompleteDate = Require.notNull(expectedDeliveryCompleteDate, "예상 납품 완료 날짜는 필수입니다.");
    }

    
    public static PO reconcile(POId id,
                               SupplierId supplierId,
                               Status status,
                               String description,
                               LocalDate orderDate,
                               LocalDate expectedDeliveryCompleteDate,
                               List<POLineState> lineStates) {
        PO po = PO.builder()
                .id(id)
                .supplierId(supplierId)
                .status(status)
                .description(description)
                .orderDate(orderDate)
                .expectedDeliveryCompleteDate(expectedDeliveryCompleteDate)
                .build();
        if (lineStates != null) {
            lineStates.forEach(po::attachLine);
        }
        return po;
    }

    public static PO draft(SupplierId supplierId, String description, LocalDate orderDate, LocalDate expectedDeliveryCompleteDate) {
        return PO.builder()
                .id(POId.forCreation())
                .supplierId(supplierId)
                .status(Status.DRAFT)
                .description(description)
                .orderDate(orderDate)
                .expectedDeliveryCompleteDate(expectedDeliveryCompleteDate)
                .build();
    }

    public void addPOLine(ItemId itemid, Quantity orderedQty, BigDecimal price, BigDecimal maxOverRatio) {
        POLine line = POLine.builder()
                .id(POLineId.forCreation())
                .itemId(itemid)
                .targetQty(orderedQty)
                .price(price)
                .overReceivePolicy(maxOverRatio == null ? OverReceivePolicy.notAllowed() : OverReceivePolicy.allowedTo(maxOverRatio))
                .build();
        lines.add(line);
    }

    //TODO: 아마 receiving 에 유스케이스에서 호출할듯? goodsReceipt 확정되거나 사후 매칭?
    public void applyReceipt(POLineId id, Quantity orderedQty) {
        Require.state(isSent() || isPartiallyReceived(), "발주 요청이 전달된 상태에서만 입고를 등록할 수 있습니다.");

        POLine line = getLineById(id);
        line.registerReceipt(orderedQty);

        if (isAllLinesFullyReceived()) {
            // close 가 무슨 의미인지가 중요할듯 우선 현재는 모든 received 가 완료되면 close
            close();
        }

        if (isSent()) {
            partiallyReceived();
        }
    }

    private boolean isAllLinesFullyReceived() {
        return this.lines.stream().allMatch(POLine::isReceivedAll);
    }

    private POLine getLineById(POLineId lineId) {
        // find 라고 하고 옵셔널쓸까? 아니면 그냥 여기서 처리?
        return lines.stream()
                .filter(l -> l.getId().equals(lineId))
                .findFirst().orElseThrow(() -> new NoSuchElementException("해당 ID의 발주 품목을 찾을 수 없습니다."));
    }

    private void attachLine(POLine line) {
        Require.notNull(line, "발주 품목은 null일 수 없습니다.");
        this.lines.add(line);
    }

    public void attachLine(POLineState state) {
        Require.notNull(state, "발주 품목 상태는 null일 수 없습니다.");
        POLine line = POLine.builder()
                .id(state.id())
                .itemId(state.itemId())
                .targetQty(state.targetQty())
                .price(state.price())
                .overReceivePolicy(state.overReceivePolicy())
                .receivedQty(state.receivedQty())
                .build();
        attachLine(line);
    }


    // 상태 전환하는 메서드들인데
    public void approve() {
        Require.state(isDraft(), "초안 상태의 발주만 승인할 수 있습니다.");
        this.status = Status.APPROVED;
    }

    public void send() {
        Require.state(isApproved(), "승인 상태의 발주만 발주 요청을 전달할 수 있습니다.");
        this.status = Status.SENT;
    }

    public void reject() {
        Require.state(isSent(), "발주 요청이 전달된 건만 공급업체에서 거절할 수 있습니다.");
        this.status = Status.REJECTED;
    }

    public void partiallyReceived() {
        Require.state(isPartiallyReceived() || isSent(), "발주 요청이 전달되었거나 부분 입고된 발주만 부분 입고 처리할 수 있습니다.");
        this.status = Status.PARTIALLY_RECEIVED;
    }

    private void receive() {
        Require.state(isSent(), "발주 요청이 전달된 발주만 입고 처리할 수 있습니다.");
        if (isAllLinesFullyReceived()) {
            close();
        } else {
            partiallyReceived();
        }
    }

    private void close() {
        Require.state(isSent() || isPartiallyReceived(), "발주 요청이 전달되었거나 부분 입고된 발주만 마감할 수 있습니다.");
        this.status = Status.CLOSED;
    }

    public void cancel() {
        //TODO: cancel
    }

    private boolean isDraft() {
        return this.status == Status.DRAFT;
    }

    private boolean isClosed() {
        // 필요한가?
        return this.status == Status.CLOSED;
    }

    private boolean isApproved() {
        return this.status == Status.APPROVED;
    }

    private boolean isSent() {
        return this.status == Status.SENT;
    }

    private boolean isRejected() {
        return this.status == Status.REJECTED;
    }

    private boolean isPartiallyReceived() {
        return this.status == Status.PARTIALLY_RECEIVED;
    }

}
