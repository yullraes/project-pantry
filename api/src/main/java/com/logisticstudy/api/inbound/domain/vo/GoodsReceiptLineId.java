package com.logisticstudy.api.inbound.domain.vo;

import com.logisticstudy.api.shared.Require;

public record GoodsReceiptLineId(Long value) {
    private static final Long SENTINEL = -2535134L;

    public GoodsReceiptLineId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "입고전표 ID는 양수여야 합니다.");
        }
    }

    public static GoodsReceiptLineId forCreation() {
        return new GoodsReceiptLineId(SENTINEL);
    }
}
