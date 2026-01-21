package com.logisticstudy.api.inbound.domain.vo;

import com.logisticstudy.api.shared.Require;

public record GoodsReceiptId(Long value) {
    private static final Long SENTINEL = -11235134L;

    public GoodsReceiptId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "입고전표 ID는 양수여야 합니다.");
        }
    }

    public static GoodsReceiptId forCreation() {
        return new GoodsReceiptId(SENTINEL);
    }
}
