package com.logisticstudy.api.masterdata.location.domain;

import com.logisticstudy.api.shared.Require;

public record LocationId(Long value) {
    private static final Long SENTINEL = -38390457983535L;

    public LocationId {
        if (value != null && !SENTINEL.equals(value)) {
            Require.positive(value, "로케이션 ID는 양수여야 합니다.");
        }
    }

    public static LocationId forCreation() {
        return new LocationId(SENTINEL);
    }
}
