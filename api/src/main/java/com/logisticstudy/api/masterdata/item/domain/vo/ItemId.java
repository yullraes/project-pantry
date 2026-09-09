package com.logisticstudy.api.masterdata.item.domain.vo;

import com.logisticstudy.api.shared.Require;

public record ItemId(Long value) {
    private static final Long SENTINEL = -28390457983535L;

    public ItemId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "품목 ID는 양수여야 합니다.");
        }
    }

    public static ItemId forCreation() {
        return new ItemId(SENTINEL);
    }
}