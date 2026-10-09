package com.personalfinance.model;

/**
 * Enum representing category types (INCOME or EXPENSE).
 */
public enum CategoryType {
    INCOME,
    EXPENSE;

    public static CategoryType fromString(String text) {
        if (text == null) return null;
        for (CategoryType type : CategoryType.values()) {
            if (type.name().equalsIgnoreCase(text.trim())) {
                return type;
            }
        }
        return null;
    }
}
