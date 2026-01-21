package com.logisticstudy.api.masterdata.location.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateLocationRequest(
        @NotBlank(message = "로케이션 코드는 비어 있을 수 없습니다.")
        String code,

        @NotBlank(message = "로케이션 이름은 비어 있을 수 없습니다.")
        String name,

        @NotBlank(message = "로케이션 타입은 비어 있을 수 없습니다.")
        String type,

        Long parentId,

        @NotNull(message = "활성 상태는 null일 수 없습니다.")
        Boolean active,

        String allowedTemperatureZone,

        List<String> allowedCategories
) {
}
