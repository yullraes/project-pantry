package com.logisticstudy.api.masterdata.item.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record UomConversionProfileRequest(
        @NotNull(message = "변환 비율 리스트는 null일 수 없습니다.")
        List<UomConversionFactorRequest> factors
) {
    public record UomConversionFactorRequest(@NotNull(message = "변환 단위는 null일 수 없습니다") String name,
                                             @NotNull(message = "변환 비율은 null일 수 없습니다") BigDecimal factor) {
    }
}
