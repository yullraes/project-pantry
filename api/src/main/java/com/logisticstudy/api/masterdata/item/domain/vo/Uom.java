package com.logisticstudy.api.masterdata.item.domain.vo;

import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 단위를 나타내는 값 객체(
 *
 * @param symbol 단위 기호 ("kg")
 * @param type   단위 종류
 */
public record Uom(String symbol, Type type) {

    public enum Type {WEIGHT, COUNT, VOLUME}

    public Uom {
        Assert.hasText(symbol, "단위 기호는 null이거나 비어 있을 수 없습니다.");

        String upperSymbol = symbol.toUpperCase();
        Type expectedType = SYMBOL_TO_TYPE_MAP.get(upperSymbol);

        // 1. Symbol 유효성 먼저 검사
        if (expectedType == null) {
            throw new IllegalArgumentException("알 수 없는 단위 기호입니다: " + symbol);
        }

        // 2. Type null 여부와 일치 여부 검사
        Assert.notNull(type, "단위 종류는 null일 수 없습니다.");
        if (type != expectedType) {
            throw new IllegalArgumentException(
                String.format("단위 기호 '%s'에 대한 타입이 일치하지 않습니다. 예상: %s, 실제: %s",
                    symbol, expectedType, type));
        }
    }

    private static final Map<String, Type> SYMBOL_TO_TYPE_MAP = Map.ofEntries(
            Map.entry("KG", Type.WEIGHT),
            Map.entry("G", Type.WEIGHT),
            Map.entry("TON", Type.WEIGHT),
            Map.entry("LB", Type.WEIGHT),
            Map.entry("EA", Type.COUNT),
            Map.entry("BOX", Type.COUNT),
            Map.entry("PLT", Type.COUNT),
            Map.entry("L", Type.VOLUME),
            Map.entry("ML", Type.VOLUME),
            Map.entry("CC", Type.VOLUME)
    );
    
    private static final Map<String, BigDecimal> CONVERSION_FACTORS = Map.ofEntries(
            Map.entry("G", BigDecimal.ONE),
            Map.entry("KG", new BigDecimal("1000")),
            Map.entry("TON", new BigDecimal("1000000")),
            Map.entry("LB", new BigDecimal("453.592")),
            Map.entry("ML", BigDecimal.ONE),
            Map.entry("L", new BigDecimal("1000")),
            Map.entry("CC", BigDecimal.ONE)
    );

    public static BigDecimal getConversionFactor(String symbol) {
        return CONVERSION_FACTORS.get(symbol.toUpperCase());
    }

    /**
     * 단위 기호(symbol)로부터 Uom 객체를 생성합니다.
     * 모든 유효성 검사는 생성자에서 처리됩니다.
     * @param symbol 단위 기호 (e.g., "kg", "ea")
     * @return Uom 객체
     */
    public static Uom of(String symbol) {
        Type determinedType = SYMBOL_TO_TYPE_MAP.get(
                symbol == null ? "" : symbol.toUpperCase()
        );
        return new Uom(symbol, determinedType);
    }

    public static Uom defaultUom() {
        return Uom.of("EA");
    }
}
