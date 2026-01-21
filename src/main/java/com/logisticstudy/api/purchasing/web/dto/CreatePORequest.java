package com.logisticstudy.api.purchasing.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePORequest(
        @NotNull(message = "공급업체 ID는 필수입니다.")
        Long supplierId,
        @Size(max = 500, message = "비고는 500자를 초과할 수 없습니다.")
        String description,
        @NotEmpty(message = "발주 품목은 최소 1개 이상이어야 합니다.")
        @Valid
        List<Item> items,
        @NotNull(message = "주문일은 필수입니다.")
        LocalDate orderDate,
        @NotNull(message = "예상 납기일은 필수입니다.")
        LocalDate expectedDeliveryDate
) {

    public record Item(
            @NotBlank(message = "SKU는 필수입니다.")
            String sku,
            @NotNull(message = "수량은 필수입니다.")
            @Positive(message = "수량은 0보다 커야 합니다.")
            BigDecimal quantity,
            @NotNull(message = "단가는 필수입니다.")
            @Positive(message = "단가는 0보다 커야 합니다.")
            BigDecimal price,
            @NotBlank(message = "단위는 필수입니다.")
            String unit,
            BigDecimal maxOverRatio
    ) {}
}
