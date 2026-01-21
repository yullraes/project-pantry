package com.logisticstudy.api.masterdata.location.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LocationResponse(
        Long id,
        String code,
        String name,
        String type,
        Long parentId,
        boolean active,
        String allowedTemperatureZone,
        List<String> allowedCategories,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime lastModifiedAt,
        String lastModifiedBy
) {
}
