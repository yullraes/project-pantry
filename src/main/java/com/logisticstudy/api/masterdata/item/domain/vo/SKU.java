package com.logisticstudy.api.masterdata.item.domain.vo;

import org.springframework.util.Assert;

// 사실상 퍼블릭 id
public record SKU(String value) {
    public SKU {
        Assert.hasText(value, "SKU 값은 null이거나 비어 있을 수 없습니다.");
    }

    public static SKU of(String value) {
        return new SKU(value);
    }
}
