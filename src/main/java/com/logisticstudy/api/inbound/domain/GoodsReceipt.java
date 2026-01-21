package com.logisticstudy.api.inbound.domain;

import com.logisticstudy.api.inbound.domain.command.*;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptId;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptLineId;
import com.logisticstudy.api.inbound.domain.vo.GoodsReceiptNumber;
import com.logisticstudy.api.inbound.domain.vo.LotInfo;
import com.logisticstudy.api.masterdata.location.domain.LocationId;
import com.logisticstudy.api.masterdata.supplier.domain.vo.SupplierId;
import com.logisticstudy.api.purchasing.domain.vo.POId;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class GoodsReceipt {
    public enum Type {
        PO_BASED,
        FREE // 구매 발주 없이 진행되는 입고
    }
    
    public enum Status {
        DRAFT,
        POSTED
    }
    private final GoodsReceiptId id;
    private final GoodsReceiptNumber grnNumber;
    private Type type;
    private Status status;
    private SupplierId supplierId;
    private POId poId;
    private final LocalDateTime receivedAt;
    private final List<GoodsReceiptLine> lines = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private GoodsReceipt(GoodsReceiptId id,
                         GoodsReceiptNumber grnNumber,
                         Type type,
                         SupplierId supplierId,
                         POId poId,
                         Status status,
                         LocalDateTime receivedAt) {
        this.id = Require.notNull(id, "입고 ID는 필수입니다.");
        this.grnNumber = Require.notNull(grnNumber, "입고 번호는 필수입니다.");
        this.type = Require.notNull(type, "입고 유형은 필수입니다.");
        this.supplierId = supplierId;
        this.poId = poId;
        this.status = Require.notNull(status, "입고 상태는 필수입니다.");
        this.receivedAt = Require.notNull(receivedAt, "입고 일시는 필수입니다.");
    }

    public static GoodsReceipt reconstitute(GoodsReceiptId id,
                                            GoodsReceiptNumber grnNumber,
                                            Type type,
                                            Status status,
                                            SupplierId supplierId,
                                            POId poId,
                                            List<GoodsReceiptLineReconstitutionParams> lineParams,
                                            LocalDateTime receivedAt) {
        GoodsReceipt receipt = new GoodsReceipt(id, grnNumber, type, supplierId, poId, status, receivedAt);
        if (lineParams != null) {
            lineParams.stream()
                    .map(GoodsReceiptLine::reconstitute)
                    .forEach(receipt.lines::add);
        }
        return receipt;
    }

    public static GoodsReceipt createFromPo(GoodsReceiptCreateFromPoParams params) {
        return GoodsReceipt.builder()
                .id(params.id())
                .grnNumber(params.grnNumber())
                .type(Type.PO_BASED)
                .status(Status.DRAFT)
                .supplierId(params.supplierId())
                .poId(params.poId())
                .receivedAt(params.receivedAt())
                .build();
    }

    public static GoodsReceipt createFreeReceipt(GoodsReceiptCreateFreeParams params) {
        return GoodsReceipt.builder()
                .id(params.id())
                .grnNumber(params.grnNumber())
                .type(Type.FREE)
                .status(Status.DRAFT)
                .supplierId(params.supplierId())
                .receivedAt(params.receivedAt())
                .build();
    }

    public GoodsReceiptLine addFreeLine(GoodsReceiptLineFreeLineParams params) {
        ensureDraft();
        GoodsReceiptLine line = GoodsReceiptLine.freeLine(params);
        this.lines.add(line);
        return line;
    }

    public GoodsReceiptLine addLineFromPo(GoodsReceiptLinePoLineParams params) {
        ensureDraft();
        GoodsReceiptLine line = GoodsReceiptLine.fromPo(params);
        this.lines.add(line);
        return line;
    }

    public void removeLine(GoodsReceiptLineId lineId) {
        ensureDraft();
        boolean removed = this.lines.removeIf(line -> Objects.equals(line.getId(), lineId));
        if (!removed) {
            throw new IllegalArgumentException("존재하지 않는 라인입니다. lineId=" + lineId.value());
        }
    }

    public void changeLineQuantity(GoodsReceiptLineId lineId, Quantity newQty) {
        ensureDraft();
        findLine(lineId).changeQuantity(newQty);
    }

    public void changeLineLocation(GoodsReceiptLineId lineId, LocationId locationId) {
        ensureDraft();
        findLine(lineId).changeLocation(locationId);
    }

    public void changeLineLotInfo(GoodsReceiptLineId lineId, LotInfo lotInfo) {
        ensureDraft();
        findLine(lineId).changeLotInfo(lotInfo);
    }

    public void linkLineToPo(GoodsReceiptLineId lineId, POLineId poLineId) {
        findLine(lineId).linkToPo(poLineId);
    }

    public void unlinkLineFromPo(GoodsReceiptLineId lineId, Long poLineId) {
        findLine(lineId).unlinkFromPo(poLineId);
    }

    public void reconcileToPo(POId newPoId) {
        POId poIdToSet = Require.notNull(newPoId, "");
        if (!this.poId.equals(poIdToSet)) {
            throw new IllegalStateException("");
        }
        this.poId = poIdToSet;
        this.type = Type.PO_BASED;
    }




    private GoodsReceiptLine findLine(GoodsReceiptLineId lineId) {
        return lines.stream()
                .filter(line -> Objects.equals(line.getId(), lineId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("라인을 찾을 수 없습니다. lineId=" + lineId.value()));
    }

    private void ensureDraft() {
        Require.state(this.status == Status.DRAFT, "작성중(DRAFT) 상태에서만 입고를 수정할 수 있습니다.");
    }

}