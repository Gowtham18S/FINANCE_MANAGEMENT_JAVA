package com.personalfinance.util;

import com.personalfinance.model.CategoryType;
import com.personalfinance.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utility class for validating user console inputs.
 */
public class InputValidator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("  [!] Please enter a number between %d and %d.\n", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a valid integer.");
            }
        }
    }

    public static long readLong(Scanner scanner, String prompt, long min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                long value = Long.parseLong(input);
                if (value >= min) {
                    return value;
                } else {
                    System.out.printf("  [!] Please enter a number greater than or equal to %d.\n", min);
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a valid long integer.");
            }
        }
    }

    public static BigDecimal readPositiveBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return value;
                } else {
                    System.out.println("  [!] Amount must be strictly greater than zero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Invalid input. Please enter a valid decimal amount (e.g., 150.00).");
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("  [!] Input cannot be empty. Please try again.");
            }
        }
    }

    public static String readOptionalString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static LocalDate readLocalDate(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (YYYY-MM-DD): ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("today") || input.equalsIgnoreCase("t")) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("  [!] Invalid date format. Please use format YYYY-MM-DD (e.g., 2026-03-15) or type 'today'.");
            }
        }
    }

    public static CategoryType readCategoryType(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (1 for INCOME, 2 for EXPENSE): ");
            String input = scanner.nextLine().trim();
            if ("1".equals(input) || "INCOME".equalsIgnoreCase(input)) {
                return CategoryType.INCOME;
            } else if ("2".equals(input) || "EXPENSE".equalsIgnoreCase(input)) {
                return CategoryType.EXPENSE;
            } else {
                System.out.println("  [!] Invalid selection. Enter 1 for INCOME or 2 for EXPENSE.");
            }
        }
    }

    public static PaymentMethod readPaymentMethod(Scanner scanner, String prompt) {
        System.out.println(prompt);
        PaymentMethod[] methods = PaymentMethod.values();
        for (int i = 0; i < methods.length; i++) {
            System.out.printf("  %d. %s\n", i + 1, methods[i].name());
        }
        int choice = readInt(scanner, "Select Payment Method (1-" + methods.length + "): ", 1, methods.length);
        return methods[choice - 1];
    }
}
