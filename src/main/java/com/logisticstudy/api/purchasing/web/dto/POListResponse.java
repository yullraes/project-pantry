package com.logisticstudy.api.purchasing.web.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record POListResponse(
        Long id,
        String supplierName,
        String status,
        LocalDate expectedDeliveryDate,
        BigDecimal totalAmount
) {
    @QueryProjection
    public POListResponse {}
}
