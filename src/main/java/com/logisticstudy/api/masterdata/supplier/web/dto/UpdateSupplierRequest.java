package com.logisticstudy.api.masterdata.supplier.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSupplierRequest(
        @NotNull(message = "공급사 ID는 null일 수 없습니다.")
        Long id,

        @NotBlank(message = "공급사 이름은 비어 있을 수 없습니다.")
        String name,

        @NotNull(message = "리드 타임은 null일 수 없습니다.")
        @Min(value = 0, message = "리드 타임은 0보다 작을 수 없습니다.")
        Integer leadTimeDays,

        @NotBlank(message = "공급사 상태는 비어 있을 수 없습니다.")
        String status,

        String address,
        String phoneNumber,

        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        String contactPersonName,

        @NotBlank(message = "사업자 등록 번호는 비어 있을 수 없습니다.")
        String businessRegistrationNumber,

        String paymentTerms
) {
}
