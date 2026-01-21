package com.logisticstudy.api.masterdata.item.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record ItemResponse(
        Long id,
        String name,
        String skuCode,
        String barcode,
        String baseUom,
        String itemCategory,
        String temperatureZone,
        boolean requiresExpiry,
        BigDecimal safetyStock,
        boolean active,
        UomConversionProfileResponse uomConversionProfile,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime lastModifiedAt,
        String lastModifiedBy
) {
    public record UomConversionProfileResponse(
            Map<String, BigDecimal> factors
    ) {
    }
}
