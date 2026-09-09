package com.logisticstudy.api.shared;

public final class Require {

    private Require() {}

    public static <T> T notNull(T value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static String notBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    public static <T extends Number> T positive(T number, String message) {
        // 오토박싱됌
        if (number == null) {
            throw new IllegalArgumentException(message);
        }
        if (number.doubleValue() <= 0) {
            throw new IllegalArgumentException(message);
        }
        return number;
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }
}
