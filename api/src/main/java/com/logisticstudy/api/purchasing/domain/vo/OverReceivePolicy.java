package com.logisticstudy.api.purchasing.domain.vo;


import com.logisticstudy.api.masterdata.item.domain.vo.Quantity;
import com.logisticstudy.api.shared.Require;
import lombok.Getter;

import java.math.BigDecimal;


public final class OverReceivePolicy {

    private final boolean allowed;
    @Getter
    private final BigDecimal maxOverRatio; // 0.05 = 5%, null이면 제한? 무제한?


    private OverReceivePolicy(boolean allowed, BigDecimal maxOverRatio) {
        if (allowed) {
            Require.notNull(maxOverRatio, "과입고 허용 시 비율은 필수입니다.");
            Require.state(maxOverRatio.compareTo(BigDecimal.ZERO) > 0 && maxOverRatio.compareTo(BigDecimal.ONE) <= 0, "과입고 허용 비율은 0보다 크고 1보다 작거나 같아야 합니다.");
        }
        this.allowed = allowed;
        this.maxOverRatio = maxOverRatio;
    }

    public static OverReceivePolicy notAllowed() {
        return new OverReceivePolicy(false, BigDecimal.ZERO);
    }

    public static OverReceivePolicy allowedTo(BigDecimal ratio) {
        return new OverReceivePolicy(true, ratio);
    }

    public boolean isAllowed() {
        return allowed;
    }


    public boolean isOverThanAllowedQty(Quantity orderedQty, Quantity newReceivedQty) {
        // orderQty.value * (1 + maxovrtRatid) 를 초과하면 안됨
        Require.notNull(orderedQty, "주문 수량은 필수입니다.");
        Require.notNull(newReceivedQty, "신규 입고 수량은 필수입니다.");

        // 단위가 다르면 일단 비교 불가능으로 처리
        if (!orderedQty.isSameType(newReceivedQty)) {
            throw new IllegalArgumentException("주문 수량과 입고 수량의 단위 종류가 다릅니다.");
        }

        if (!allowed) {
            return newReceivedQty.isGreaterThan(orderedQty);
        }

        BigDecimal allowedMaxQtyValue = orderedQty.value().multiply(BigDecimal.ONE.add(maxOverRatio));
        Quantity allowedMaxQty = new Quantity(allowedMaxQtyValue, orderedQty.uom());

        return newReceivedQty.isGreaterThan(allowedMaxQty);
    }
}
