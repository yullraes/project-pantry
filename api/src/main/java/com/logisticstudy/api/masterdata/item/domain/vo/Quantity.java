package com.logisticstudy.api.masterdata.item.domain.vo;

import com.logisticstudy.api.shared.Require;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 측정된 수량과 단위를 함께 나타내는 값 객체
 * @param value 측정된 수량
 * @param uom 사용된 단위
 */
public record Quantity(BigDecimal value, Uom uom) implements Comparable<Quantity> {
    public Quantity {
        Require.notNull(value, "수량 값은 필수입니다.");
        Require.state(value.compareTo(BigDecimal.ZERO) >= 0, "수량은 음수가 될 수 없습니다.");
        Require.notNull(uom, "수량 단위는 필수입니다.");
    }

    public static Quantity of(BigDecimal value, Uom uom) {
        return new Quantity(value, uom);
    }

    public static Quantity Zero() {
        return new Quantity(BigDecimal.ZERO, Uom.defaultUom());
    }
    
    /**
     * 다른 Quantity와 단위 종류(UomType)가 동일한지 확인합니다.
     * @param other 비교할 다른 Quantity 객체
     * @return 단위 종류가 같으면 true, 아니면 false
     */
    public boolean isSameType(Quantity other) {
        if (other == null) {
            return false;
        }
        return this.uom.type() == other.uom.type();
    }

    /**
     * 동일한 단위 종류(Uom.Type) 내에서 단위를 변환합니다. (예: g <-> kg)
     * @param targetUom 변환할 단위
     * @return 변환된 Quantity 객체
     */
    public Quantity convertWithinSameType(Uom targetUom) {
        targetUom = Require.notNull(targetUom, "변환할 단위는 null일 수 없습니다.");

        if (this.uom.type() != targetUom.type()) {
            throw new IllegalArgumentException(
                    String.format("단위 종류가 달라 자동 변환이 불가능합니다: '%s' -> '%s'. 명시적인 변환 비율(factor)이 필요한 변환입니다.", this.uom.type(), targetUom.type())
            );
        }

        if (this.uom.equals(targetUom)) {
            return this;
        }

        BigDecimal sourceFactor = Uom.getConversionFactor(this.uom.symbol());
        BigDecimal targetFactor = Uom.getConversionFactor(targetUom.symbol());

        if (sourceFactor == null || targetFactor == null) {
            throw new UnsupportedOperationException(
                    String.format("단위 변환을 지원하지 않습니다: %s -> %s", this.uom.symbol(), targetUom.symbol())
            );
        }

        BigDecimal baseValue = this.value.multiply(sourceFactor);
        BigDecimal newValue = baseValue.divide(targetFactor, 5, RoundingMode.HALF_UP);

        return new Quantity(newValue, targetUom);
    }

    /**
     * 명시적인 변환 비율(factor)을 사용하여 단위를 변환합니다.
     * 아이템별 변환(예: L->kg)이나 복합 단위 변환에 사용됩니다.
     * @param targetUom 변환할 단위
     * @param conversionFactor 소스 단위 1단위당 타겟 단위의 양 (예: 1L가 0.92kg일 경우, 0.92를 인자로 전달)
     * @return 변환된 Quantity 객체
     */
    public Quantity convertByFactor(Uom targetUom, BigDecimal conversionFactor) {
        targetUom = Require.notNull(targetUom, "변환할 단위는 null일 수 없습니다.");
        conversionFactor = Require.notNull(conversionFactor, "변환 비율은 null일 수 없습니다.");
        Require.positive(conversionFactor, "변환 비율은 0보다 커야 합니다.");

        BigDecimal newValue = this.value.multiply(conversionFactor);
        return new Quantity(newValue, targetUom);
    }

    public Quantity add(Quantity qty) {
        //TODO: 이거 어카냐 변환 해줘야하나? 일단 throw
        Require.state(isSameType(qty), "수량의 단위 종류가 다릅니다. 동일한 종류의 단위만 더할 수 있습니다.");

        return new Quantity(this.value.add(qty.value), this.uom);
    }

    public Quantity subtract(Quantity qty) {
        Require.state(isSameType(qty), "수량의 단위 종류가 다릅니다. 동일한 종류의 단위만 뺄 수 있습니다.");
        BigDecimal newValue = this.value.subtract(qty.value);
        return new Quantity(newValue, this.uom);
    }

    public boolean isAtLeast(Quantity qty) {
        Require.state(isSameType(qty),  "수량의 단위 종류가 다릅니다. 동일한 종류의 단위만 비교할 수 있습니다.");
        return this.value.compareTo(qty.value) >= 0;
    }

    public boolean isGreaterThan(Quantity qty) {
        Require.state(isSameType(qty), "수량의 단위 종류가 다릅니다. 동일한 종류의 단위만 비교할 수 있습니다.");
        return this.value.compareTo(qty.value) > 0;
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value.compareTo(o.value);
    }
}
