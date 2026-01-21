package com.logisticstudy.api.stock.domain.vo;

import com.logisticstudy.api.shared.Require;

public record LotId(Long value) {
    public LotId {
        Require.positive(value, "Lot ID는 양수여야 합니다.");
    }
}
