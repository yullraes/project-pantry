package com.logisticstudy.api.masterdata.location.web;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemCategory;
import com.logisticstudy.api.masterdata.location.domain.Location;
import com.logisticstudy.api.masterdata.location.web.dto.CreateLocationRequest;
import com.logisticstudy.api.masterdata.location.web.dto.LocationResponse;
import com.logisticstudy.api.masterdata.location.web.dto.UpdateLocationRequest;
import com.logisticstudy.api.masterdata.vo.TemperatureZone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface LocationDtoMapper {

    @Mappings({
            @Mapping(source = "type", target = "type"),
            @Mapping(source = "allowedTemperatureZone", target = "allowedTemperatureZone"),
            @Mapping(source = "allowedCategories", target = "allowedCategories")
    })
    LocationResponse toResponse(Location location);

    default Location toDomain(CreateLocationRequest request, Long newId, String creator) {
        return Location.builder()
                .id(newId)
                .code(request.code())
                .name(request.name())
                .type(Location.Type.valueOf(request.type().toUpperCase()))
                .parentId(request.parentId())
                .active(request.active())
                .allowedTemperatureZone(TemperatureZone.valueOf(request.allowedTemperatureZone().toUpperCase()))
                .allowedCategories(request.allowedCategories().stream().map(cat -> ItemCategory.valueOf(cat.toUpperCase())).toList())
                .build();
    }

    default Location toDomain(UpdateLocationRequest request, Location existingLocation, String updater) {
        return Location.builder()
                .id(existingLocation.getId())
                .code(request.code())
                .name(request.name())
                .type(Location.Type.valueOf(request.type().toUpperCase()))
                .parentId(request.parentId())
                .active(request.active())
                .allowedTemperatureZone(TemperatureZone.valueOf(request.allowedTemperatureZone().toUpperCase()))
                .allowedCategories(request.allowedCategories().stream().map(cat -> ItemCategory.valueOf(cat.toUpperCase())).toList())
                .build();
    }
}