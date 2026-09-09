package com.logisticstudy.api.masterdata.location.infra;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemCategory;
import com.logisticstudy.api.masterdata.location.domain.Location;
import com.logisticstudy.api.masterdata.location.infra.entity.LocationEntity;
import com.logisticstudy.api.masterdata.vo.TemperatureZone;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationEntityMapper {

    private static final String CATEGORY_DELIMITER = ",";

    public Location toDomain(LocationEntity entity) {

        return Location.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .type(Location.Type.valueOf(entity.getType().toUpperCase()))
                .parentId(entity.getParentId())
                .active(entity.isActive())
                .allowedTemperatureZone(TemperatureZone.valueOf(entity.getAllowedTemperatureZone().toUpperCase()))
                .allowedCategories(mapToItemCategoryList(entity.getAllowedCategories()))
                .build();
    }

    public LocationEntity toEntity(Location domain) {
        return LocationEntity.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .type(domain.getType().name())
                .parentId(domain.getParentId())
                .active(domain.isActive())
                .allowedTemperatureZone(domain.getAllowedTemperatureZone().name())
                .allowedCategories(mapToString(domain.getAllowedCategories()))
                .build();
    }

    private List<ItemCategory> mapToItemCategoryList(String categories) {
        // 도메인에서 유효성 검증을 하므로, 여기서는 null/empty 체크를 하지 않습니다.
        return Arrays.stream(categories.split(CATEGORY_DELIMITER))
                .map(String::trim)
                .map(cat -> ItemCategory.valueOf(cat.toUpperCase()))
                .toList();
    }

    private String mapToString(List<ItemCategory> categories) {
        // 도메인에서 유효성 검증을 하므로, 여기서는 null/empty 체크를 하지 않습니다.
        return categories.stream()
                .map(Enum::name)
                .collect(Collectors.joining(CATEGORY_DELIMITER));
    }
}