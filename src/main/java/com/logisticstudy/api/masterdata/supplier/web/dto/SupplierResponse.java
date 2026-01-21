package com.logisticstudy.api.masterdata.supplier.web.dto;

import java.time.LocalDateTime;

public record SupplierResponse(
        Long id,
        String name,
        Integer leadTimeDays,
        String status,
        String address,
        String phoneNumber,
        String email,
        String contactPersonName,
        String businessRegistrationNumber,
        String paymentTerms,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime lastModifiedAt,
        String lastModifiedBy
) {
}
