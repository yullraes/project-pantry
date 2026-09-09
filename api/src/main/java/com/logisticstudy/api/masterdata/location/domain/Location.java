package com.logisticstudy.api.masterdata.location.domain;

import com.logisticstudy.api.masterdata.item.domain.vo.ItemCategory;
import com.logisticstudy.api.masterdata.vo.TemperatureZone;
import com.logisticstudy.api.shared.Require;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class Location {
    public enum Type {WH, BIN, STAGE, REJECT} // stage는 임시 reject는 반품용도

    private LocationId id;
    private String code;
    private String name;
    private Type type;
    private LocationId parentId;
    private boolean active; // 나중에 enum
    private TemperatureZone allowedTemperatureZone;
    private List<ItemCategory> allowedCategories;

    @Builder(access = AccessLevel.PACKAGE)
    Location(LocationId id, String code, String name, Type type, LocationId parentId, boolean active,
             TemperatureZone allowedTemperatureZone, List<ItemCategory> allowedCategories) {
        this.id = Require.notNull(id, "로케이션 ID는 필수입니다.");
        this.code = Require.notBlank(code, "로케이션 코드는 필수입니다.");
        this.name = Require.notBlank(name, "로케이션 이름은 필수입니다.");
        this.type = Require.notNull(type, "로케이션 타입은 필수입니다.");
        this.parentId = parentId;
        this.active = active;
        this.allowedTemperatureZone = allowedTemperatureZone;
        this.allowedCategories = allowedCategories == null ? Collections.emptyList() : Collections.unmodifiableList(allowedCategories);
    }

    public static Location reconcile(LocationId id, String code, String name, Type type, LocationId parentId, boolean active,
                                     TemperatureZone allowedTemperatureZone, List<ItemCategory> allowedCategories) {
        return Location.builder()
                .id(id)
                .code(code)
                .name(name)
                .type(type)
                .parentId(parentId)
                .active(active)
                .allowedTemperatureZone(allowedTemperatureZone)
                .allowedCategories(allowedCategories)
                .build();
    }

    public static Location create(String code, String name, Type type, LocationId parentId,
                                  TemperatureZone allowedTemperatureZone, List<ItemCategory> allowedCategories) {
        return Location.builder()
                .id(LocationId.forCreation())
                .code(code)
                .name(name)
                .type(type)
                .parentId(parentId)
                .active(true)
                .allowedTemperatureZone(allowedTemperatureZone)
                .allowedCategories(allowedCategories)
                .build();
    }

    /**
     * 이 로케이션에 특정 속성의 상품을 보관할 수 있는지 확인합니다.
     * (단, 이 로케이션의 allowedTemperatureZone은 null이 아니고, allowedCategories는 비어있지 않다고 가정합니다.)
     *
     * @param itemTemperatureZone 상품의 온도대
     * @param itemCategory        상품의 카테고리
     * @return 보관 가능하면 true, 불가능하면 false
     */
    public boolean canStore(TemperatureZone itemTemperatureZone, ItemCategory itemCategory) {
        Require.notNull(itemTemperatureZone, "상품의 온도대는 필수입니다.");
        Require.notNull(itemCategory, "상품의 카테고리는 필수입니다.");

        // 온도대 제약 조건 확인
        if (this.allowedTemperatureZone != null && this.allowedTemperatureZone != itemTemperatureZone) {
            return false;
        }

        // 카테고리 제약 조건 확인
        if (this.allowedCategories != null && !this.allowedCategories.isEmpty() && !this.allowedCategories.contains(itemCategory)) {
            return false;
        }

        return true;
    }
}
