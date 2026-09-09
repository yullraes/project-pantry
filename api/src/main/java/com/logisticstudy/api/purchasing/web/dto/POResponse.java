package com.logisticstudy.api.purchasing.web.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record POResponse(
        Long id,
        String supplierName,
        String createdBy,
        String status,
        LocalDate orderDate,
        LocalDate expectedDeliveryDate,
        BigDecimal totalAmount,
        List<Line> lines
) {
    @QueryProjection
    public POResponse {}

    @Builder
    public record Line(
            String itemName,
            String sku,
            BigDecimal orderedQty,
            BigDecimal receivedQty,
            BigDecimal remainingQty,
            BigDecimal unitPrice,
            BigDecimal amount
    ) {
        @QueryProjection
        public Line {}
    }
}
