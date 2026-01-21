package com.logisticstudy.api.purchasing.domain.vo;

import com.logisticstudy.api.shared.Require;

public record POId(Long value) {
    private static final Long SENTINEL = -28390457983535L;

    public POId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "발주 ID는 양수여야 합니다.");
        }
    }

    public static POId forCreation() {
        return new POId(SENTINEL);
    }
}
