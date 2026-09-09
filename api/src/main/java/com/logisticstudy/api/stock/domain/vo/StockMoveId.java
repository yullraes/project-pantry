package com.logisticstudy.api.stock.domain.vo;

import com.logisticstudy.api.shared.Require;

public record StockMoveId(Long value) {
    private static final Long SENTINEL = -1838784L;

    public StockMoveId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "재고 이동 ID는 양수여야 합니다.");
        }

    }

    public static StockMoveId forCreation() {
        return new StockMoveId(SENTINEL);
    }
}
