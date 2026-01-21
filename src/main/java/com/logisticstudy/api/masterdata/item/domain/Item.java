package com.logisticstudy.api.masterdata.item.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.*;
import com.logisticstudy.api.masterdata.vo.TemperatureZone;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Item {
    private ItemId id;
    private SKU sku;
    private String name;
    private Uom baseUom;
    private ItemCategory itemCategory;
    private TemperatureZone temperatureZone;
    private boolean requiresExpiry;
    private BigDecimal safetyStock;
    private boolean active;

    private UomConversionProfile uomConversionProfile;

    @Builder(access = AccessLevel.PRIVATE)
    private Item(ItemId id,
         String name,
         SKU sku,
         Uom baseUom,
         ItemCategory itemCategory,
         TemperatureZone temperatureZone,
         boolean requiresExpiry,
         BigDecimal safetyStock,
         boolean active,
         UomConversionProfile uomConversionProfile) {

        this.id = Require.notNull(id, "품목 ID는 필수입니다.");
        this.name = Require.notBlank(name, "이름은 필수입니다.");
        this.sku = Require.notNull(sku, "SKU는 필수입니다.");
        this.baseUom = Require.notNull(baseUom, "기본 단위(baseUom)는 필수입니다.");
        this.uomConversionProfile = Require.notNull(uomConversionProfile, "단위 변환 프로필은 필수입니다.");
        this.itemCategory = itemCategory;
        this.temperatureZone = temperatureZone;
        this.requiresExpiry = requiresExpiry;
        this.safetyStock = safetyStock;
        this.active = active;
    }

    public static Item reconcile(ItemId id,
                                 String name,
                                 SKU sku,
                                 Uom baseUom,
                                 ItemCategory itemCategory,
                                 TemperatureZone temperatureZone,
                                 boolean requiresExpiry,
                                 BigDecimal safetyStock,
                                 boolean active,
                                 UomConversionProfile uomConversionProfile) {
        return Item.builder()
                .id(id)
                .name(name)
                .sku(sku)
                .baseUom(baseUom)
                .itemCategory(itemCategory)
                .temperatureZone(temperatureZone)
                .requiresExpiry(requiresExpiry)
                .safetyStock(safetyStock)
                .active(active)
                .uomConversionProfile(uomConversionProfile)
                .build();
    }

    public static Item create(String name,
                              SKU sku,
                              Uom baseUom,
                              ItemCategory itemCategory,
                              TemperatureZone temperatureZone,
                              boolean requiresExpiry,
                              BigDecimal safetyStock,
                              UomConversionProfile uomConversionProfile) {
        return Item.builder()
                .id(ItemId.forCreation())
                .name(name)
                .sku(sku)
                .baseUom(baseUom)
                .itemCategory(itemCategory)
                .temperatureZone(temperatureZone)
                .requiresExpiry(requiresExpiry)
                .safetyStock(safetyStock)
                .active(true)
                .uomConversionProfile(uomConversionProfile)
                .build();
    }


    /**
     * 이 아이템의 단위 변환 규칙에 따라 Measurement를 다른 단위로 변환합니다.
     *
     * @param quantity 변환할 측정 객체z
     * @param targetUom   변환 목표 단위
     * @return 변환된 Quantity 객체
     * @throws IllegalArgumentException      변환이 불가능하거나 필요한 비율 정보가 없을 경우
     * @throws UnsupportedOperationException 보편적 변환이 지원되지 않는 경우
     */
    public Quantity convert(Quantity quantity, Uom targetUom) {
        Require.notNull(quantity, "변환할 수량은 필수입니다.");
        Require.notNull(targetUom, "변환 목표 단위는 필수입니다.");
        Require.notNull(this.baseUom, "품목에 기본 단위가 설정되어 있지 않아 변환을 수행할 수 없습니다.");

        Uom fromUom = quantity.uom();

        // 변환이 필요 없는 경우
        if (fromUom.equals(targetUom)) {
            return quantity;
        }

        // 타입이 같고, COUNT가 아닌 경우 (WEIGHT, VOLUME)에만 보편적 변환 시도
        if (fromUom.type() == targetUom.type() && fromUom.type() != Uom.Type.COUNT) {
            return quantity.convertWithinSameType(targetUom);
        }

        // 그 외 모든 경우
        BigDecimal fromFactor = fromUom.equals(this.baseUom)
                ? BigDecimal.ONE
                : uomConversionProfile.getFactorFor(fromUom);

        BigDecimal toFactor = targetUom.equals(this.baseUom)
                ? BigDecimal.ONE
                : uomConversionProfile.getFactorFor(targetUom);

        // TODO: 반올림 아이템 따라 정책 다르게 하는거 고려 해봐야함
        BigDecimal valueInBase = quantity.value().multiply(fromFactor);
        BigDecimal newValue = valueInBase.divide(toFactor, 5, RoundingMode.HALF_UP);

        return new Quantity(newValue, targetUom);
    }
}