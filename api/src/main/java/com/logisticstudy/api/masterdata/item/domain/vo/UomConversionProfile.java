package com.logisticstudy.api.masterdata.item.domain.vo;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Item의 baseUom을 기준으로 다른 단위들의 변환 비율을 정의하는 프로필.
 */
public record UomConversionProfile(Set<UomConversionFactor> factors) {

    public UomConversionProfile {
        Assert.notNull(factors, "변환 비율 Set은 null일 수 없습니다.");
    }

    public BigDecimal getFactorFor(Uom uom) {
        return findFactorFor(uom)
                .orElseThrow(() -> new IllegalArgumentException("'" + uom.symbol() + "' 단위에 대한 변환 비율을 찾을 수 없습니다."));
    }

    public Optional<BigDecimal> findFactorFor(Uom uom) {
        Assert.notNull(uom, "Uom은 null일 수 없습니다.");
        return factors.stream()
                .filter(factor -> factor.toUnit().equals(uom))
                .findFirst()
                .map(UomConversionFactor::toFactor);
    }
}