package com.logisticstudy.api.masterdata.item.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateItemRequest(
        @NotNull(message = "아이템 ID는 null일 수 없습니다.")
        Long id,

        @NotBlank(message = "아이템 이름은 비어 있을 수 없습니다.")
        String name,

        @NotBlank(message = "SKU 코드는 비어 있을 수 없습니다.")
        String skuCode,

        String barcode,

        @NotBlank(message = "기본 단위는 비어 있을 수 없습니다.")
        String baseUom,

        @NotBlank(message = "아이템 카테고리는 비어 있을 수 없습니다.")
        String itemCategory,

        @NotBlank(message = "온도대는 비어 있을 수 없습니다.")
        String temperatureZone,

        @NotNull(message = "유통기한 필요 여부는 null일 수 없습니다.")
        Boolean requiresExpiry,

        BigDecimal safetyStock,

        @NotNull(message = "활성 상태는 null일 수 없습니다.")
        Boolean active,

        @NotNull(message = "단위 변환 프로필은 null일 수 없습니다.")
        @Valid
        UomConversionProfileRequest uomConversionProfile
) {
}
