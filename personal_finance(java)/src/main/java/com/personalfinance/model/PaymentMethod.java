package com.personalfinance.model;

/**
 * Enum representing supported payment methods.
 */
public enum PaymentMethod {
    CASH,
    UPI,
    DEBIT_CARD,
    CREDIT_CARD,
    BANK_TRANSFER,
    OTHER;

    public static PaymentMethod fromString(String text) {
        if (text == null) return null;
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(text.trim())) {
                return pm;
            }
        }
        return null;
    }
}
