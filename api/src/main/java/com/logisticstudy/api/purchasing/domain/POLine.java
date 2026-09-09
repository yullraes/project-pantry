package com.logisticstudy.api.purchasing.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.masterdata.item.domain.vo.ItemId;
import com.logisticstudy.api.purchasing.domain.vo.OverReceivePolicy;
import com.logisticstudy.api.purchasing.domain.vo.POLineId;
import com.logisticstudy.api.shared.Require;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class POLine {


    private final POLineId id;
    private final ItemId itemId;
    private final Quantity targetQty; // 원래 발주넣은 양, 받아야할 양
    private Quantity receivedQty; // 여태 받은 양, 즉 누적 양
    private final BigDecimal price; // 값객체로 바꿀까?
    private final OverReceivePolicy overReceivePolicy;

    @Builder
    POLine(POLineId id, ItemId itemId, Quantity targetQty, BigDecimal price, OverReceivePolicy overReceivePolicy, Quantity receivedQty) {
        this.id = Require.notNull(id, "발주 품목 ID는 필수입니다.");
        this.itemId = Require.notNull(itemId, "품목 ID는 필수입니다.");
        this.targetQty = Require.notNull(targetQty, "주문 수량은 필수입니다.");
        this.price = Require.positive(price, "가격은 양수여야 합니다.");
        this.overReceivePolicy = overReceivePolicy != null ? overReceivePolicy : OverReceivePolicy.notAllowed();

        if (receivedQty != null) {
            Require.state(receivedQty.isSameType(targetQty), "입고 수량의 단위가 주문 단위와 다릅니다.");
            Require.state(!receivedQty.isGreaterThan(targetQty), "입고 누적 수량이 주문 수량을 초과했습니다.");
            this.receivedQty = receivedQty;
        } else {
            this.receivedQty = Quantity.of(BigDecimal.ZERO, targetQty.uom());
        }
    }

    void registerReceipt(Quantity qty) {
        Require.notNull(qty, "입고 수량은 필수입니다.");
        Quantity newReceivedQty = this.receivedQty.add(qty);

        if (overReceivePolicy.isOverThanAllowedQty(targetQty, newReceivedQty)) {
            throw new IllegalStateException("과입고 허용 정책을 위반하였습니다.");
        }
        this.receivedQty = newReceivedQty;
    }

    boolean isReceivedAll() {
        return this.receivedQty.isAtLeast(this.targetQty);
    }

}
