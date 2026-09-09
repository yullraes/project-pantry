package com.logisticstudy.api.masterdata.item.infra;

import com.logisticstudy.api.masterdata.item.domain.Item;
import com.logisticstudy.api.masterdata.item.domain.vo.*;
import com.logisticstudy.api.masterdata.item.infra.entity.ItemEntity;
import com.logisticstudy.api.masterdata.vo.TemperatureZone;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ItemEntityMapper {

    public Item toDomain(ItemEntity entity) {
        return Item.builder()
                .id(entity.getId())
                .name(entity.getName())
                .sku(new SKU(entity.getSkuCode()))
                .baseUom(Uom.of(entity.getBaseUom()))
                .itemCategory(ItemCategory.valueOf(entity.getItemCategory().toUpperCase()))
                .temperatureZone(TemperatureZone.valueOf(entity.getTemperatureZone().toUpperCase()))
                .requiresExpiry(entity.isRequiresExpiry())
                .safetyStock(entity.getSafetyStock())
                .active(entity.isActive())
                .uomConversionProfile(mapUomConversionProfile(entity))
                .build();
    }

    public ItemEntity toEntity(Item domain) {
        UomConversionProfile profile = domain.getUomConversionProfile();
        return ItemEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .skuCode(domain.getSku().value())
                .baseUom(domain.getBaseUom().symbol())
                .itemCategory(domain.getItemCategory().name())
                .temperatureZone(domain.getTemperatureZone().name())
                .requiresExpiry(domain.isRequiresExpiry())
                .safetyStock(domain.getSafetyStock())
                .active(domain.isActive())
                .eaToGram(profile.findFactorFor(Uom.of("g")).orElse(null))
                .eaToMl(profile.findFactorFor(Uom.of("ml")).orElse(null))
                .eaToBox(profile.findFactorFor(Uom.of("box")).orElse(null))
                .eaToPlt(profile.findFactorFor(Uom.of("plt")).orElse(null))
                .build();
    }

    private UomConversionProfile mapUomConversionProfile(ItemEntity entity) {
        Set<UomConversionFactor> factors = new HashSet<>();
        if (entity.getEaToGram() != null) factors.add(new UomConversionFactor(Uom.of("g"), entity.getEaToGram()));
        if (entity.getEaToMl() != null) factors.add(new UomConversionFactor(Uom.of("ml"), entity.getEaToMl()));
        if (entity.getEaToBox() != null) factors.add(new UomConversionFactor(Uom.of("box"), entity.getEaToBox()));
        if (entity.getEaToPlt() != null) factors.add(new UomConversionFactor(Uom.of("plt"), entity.getEaToPlt()));
        return new UomConversionProfile(factors);
    }
}