package com.logisticstudy.api.masterdata.supplier.domain.vo;

import com.logisticstudy.api.shared.Require;

public record SupplierId(Long value) {
    private static final Long SENTINEL = -48390457983535L;

    public SupplierId {
        if (!SENTINEL.equals(value)) {
            Require.positive(value, "공급업체 ID는 양수여야 합니다.");
        }
    }

    public static SupplierId forCreation() {
        return new SupplierId(SENTINEL);
    }
}
