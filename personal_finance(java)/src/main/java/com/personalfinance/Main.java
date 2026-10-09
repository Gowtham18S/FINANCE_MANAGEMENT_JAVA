package com.personalfinance;

import com.personalfinance.model.Budget;
import com.personalfinance.model.BudgetSummary;
import com.personalfinance.model.Category;
import com.personalfinance.model.CategoryType;
import com.personalfinance.model.Expense;
import com.personalfinance.model.FinancialSummary;
import com.personalfinance.model.Income;
import com.personalfinance.model.PaymentMethod;
import com.personalfinance.service.BudgetService;
import com.personalfinance.service.CategoryService;
import com.personalfinance.service.ExpenseService;
import com.personalfinance.service.IncomeService;
import com.personalfinance.service.SummaryService;
import com.personalfinance.util.DatabaseInitializer;
import com.personalfinance.util.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console-based Entry Point for Personal Finance Management System.
 */
public class Main {

    private static CategoryService categoryService;
    private static IncomeService incomeService;
    private static ExpenseService expenseService;
    private static BudgetService budgetService;
    private static SummaryService summaryService;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  PERSONAL FINANCE MANAGEMENT SYSTEM (CORE JAVA)  ");
        System.out.println("==================================================");

        // Initialize Database & Seed Data
        boolean initialized = DatabaseInitializer.initializeDatabase();
        if (!initialized) {
            System.err.println("[FATAL] Database initialization failed. Please check MySQL setup and credentials.");
            System.exit(1);
        }

        // Initialize Services
        categoryService = new CategoryService();
        incomeService = new IncomeService();
        expenseService = new ExpenseService();
        budgetService = new BudgetService();
        summaryService = new SummaryService();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = InputValidator.readInt(scanner, "Enter your choice (1-7): ", 1, 7);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> handleIncomeSubmenu(scanner);
                    case 2 -> handleExpenseSubmenu(scanner);
                    case 3 -> handleCategorySubmenu(scanner);
                    case 4 -> handleBudgetSubmenu(scanner);
                    case 5 -> handleFinancialSummarySubmenu(scanner);
                    case 6 -> handleSearchTransactionsSubmenu(scanner);
                    case 7 -> {
                        running = false;
                        System.out.println("Thank you for using Personal Finance Management System. Goodbye!");
                    }
                }
            } catch (Exception e) {
                System.out.println("\n[ERROR] An unexpected error occurred: " + e.getMessage());
                System.out.println("Returning to main menu...\n");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("========================================");
        System.out.println("   PERSONAL FINANCE MANAGEMENT SYSTEM   ");
        System.out.println("========================================");
        System.out.println("1. Income Management");
        System.out.println("2. Expense Management");
        System.out.println("3. Category Management");
        System.out.println("4. Budget Management");
        System.out.println("5. Financial Summary");
        System.out.println("6. Search Transactions");
        System.out.println("7. Exit");
        System.out.println("========================================");
    }

    // ===================================================
    // 1. INCOME SUBMENU
    // ===================================================
    private static void handleIncomeSubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- INCOME MANAGEMENT ---");
            System.out.println("1. Add Income");
            System.out.println("2. View All Income");
            System.out.println("3. Search Income by ID");
            System.out.println("4. Update Income");
            System.out.println("5. Delete Income");
            System.out.println("6. Search Income by Category");
            System.out.println("7. View Monthly Income");
            System.out.println("8. View Annual Income");
            System.out.println("9. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-9): ", 1, 9);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> addIncome(scanner);
                    case 2 -> viewAllIncome();
                    case 3 -> searchIncomeById(scanner);
                    case 4 -> updateIncome(scanner);
                    case 5 -> deleteIncome(scanner);
                    case 6 -> searchIncomeByCategory(scanner);
                    case 7 -> viewMonthlyIncome(scanner);
                    case 8 -> viewAnnualIncome(scanner);
                    case 9 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Income operation failed: " + e.getMessage());
            }
        }
    }

    private static void addIncome(Scanner scanner) throws Exception {
        System.out.println("--- Add Income Record ---");
        String source = InputValidator.readNonEmptyString(scanner, "Enter Income Source: ");
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter Amount: ");
        LocalDate date = InputValidator.readLocalDate(scanner, "Enter Income Date");
        displayCategories(CategoryType.INCOME);
        long categoryId = InputValidator.readLong(scanner, "Enter Income Category ID: ", 1);
        String description = InputValidator.readOptionalString(scanner, "Enter Description (Optional): ");

        Income income = incomeService.addIncome(source, amount, date, categoryId, description);
        System.out.println("[SUCCESS] Income record added successfully with ID: " + income.getId());
    }

    private static void viewAllIncome() throws Exception {
        List<Income> incomes = incomeService.getAllIncome();
        System.out.println("--- All Income Records ---");
        if (incomes.isEmpty()) {
            System.out.println("No income records found.");
        } else {
            incomes.forEach(System.out::println);
        }
    }

    private static void searchIncomeById(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Income ID: ", 1);
        Income income = incomeService.getIncomeById(id);
        System.out.println("--- Income Found ---");
        System.out.println(income);
    }

    private static void updateIncome(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Income ID to Update: ", 1);
        Income existing = incomeService.getIncomeById(id);
        System.out.println("Current Record: " + existing);

        String source = InputValidator.readNonEmptyString(scanner, "Enter New Source [" + existing.getSource() + "]: ");
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter New Amount [" + existing.getAmount() + "]: ");
        LocalDate date = InputValidator.readLocalDate(scanner, "Enter New Date [" + existing.getIncomeDate() + "]");
        displayCategories(CategoryType.INCOME);
        long categoryId = InputValidator.readLong(scanner, "Enter New Category ID [" + existing.getCategoryId() + "]: ", 1);
        String description = InputValidator.readOptionalString(scanner, "Enter New Description: ");

        boolean updated = incomeService.updateIncome(id, source, amount, date, categoryId, description);
        if (updated) {
            System.out.println("[SUCCESS] Income record updated successfully.");
        } else {
            System.out.println("[FAILED] Could not update income record.");
        }
    }

    private static void deleteIncome(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Income ID to Delete: ", 1);
        Income existing = incomeService.getIncomeById(id);
        System.out.println("Record to delete: " + existing);
        String confirm = InputValidator.readNonEmptyString(scanner, "Are you sure you want to delete this record? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            boolean deleted = incomeService.deleteIncome(id);
            if (deleted) {
                System.out.println("[SUCCESS] Income record deleted successfully.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void searchIncomeByCategory(Scanner scanner) throws Exception {
        displayCategories(CategoryType.INCOME);
        long categoryId = InputValidator.readLong(scanner, "Enter Income Category ID: ", 1);
        List<Income> incomes = incomeService.getIncomeByCategory(categoryId);
        System.out.println("--- Income Records for Category ID " + categoryId + " ---");
        if (incomes.isEmpty()) {
            System.out.println("No income records found for this category.");
        } else {
            incomes.forEach(System.out::println);
        }
    }

    private static void viewMonthlyIncome(Scanner scanner) throws Exception {
        int month = InputValidator.readInt(scanner, "Enter Month (1-12): ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        BigDecimal total = incomeService.getTotalIncomeByMonth(month, year);
        System.out.printf("\n>>> Total Income for %02d/%d: $%.2f <<<\n", month, year, total);
    }

    private static void viewAnnualIncome(Scanner scanner) throws Exception {
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        BigDecimal total = incomeService.getTotalIncomeByYear(year);
        System.out.printf("\n>>> Total Income for Year %d: $%.2f <<<\n", year, total);
    }


    // ===================================================
    // 2. EXPENSE SUBMENU
    // ===================================================
    private static void handleExpenseSubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- EXPENSE MANAGEMENT ---");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Search Expense by ID");
            System.out.println("4. Update Expense");
            System.out.println("5. Delete Expense");
            System.out.println("6. Search Expenses by Category");
            System.out.println("7. View Monthly Expenses");
            System.out.println("8. View Annual Expenses");
            System.out.println("9. View Highest Expenses");
            System.out.println("10. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-10): ", 1, 10);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> addExpense(scanner);
                    case 2 -> viewAllExpenses();
                    case 3 -> searchExpenseById(scanner);
                    case 4 -> updateExpense(scanner);
                    case 5 -> deleteExpense(scanner);
                    case 6 -> searchExpensesByCategory(scanner);
                    case 7 -> viewMonthlyExpenses(scanner);
                    case 8 -> viewAnnualExpenses(scanner);
                    case 9 -> viewHighestExpenses(scanner);
                    case 10 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Expense operation failed: " + e.getMessage());
            }
        }
    }

    private static void addExpense(Scanner scanner) throws Exception {
        System.out.println("--- Add Expense Record ---");
        String title = InputValidator.readNonEmptyString(scanner, "Enter Expense Title: ");
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter Amount: ");
        LocalDate date = InputValidator.readLocalDate(scanner, "Enter Expense Date");
        displayCategories(CategoryType.EXPENSE);
        long categoryId = InputValidator.readLong(scanner, "Enter Expense Category ID: ", 1);
        PaymentMethod paymentMethod = InputValidator.readPaymentMethod(scanner, "Select Payment Method:");
        String description = InputValidator.readOptionalString(scanner, "Enter Description (Optional): ");

        Expense expense = expenseService.addExpense(title, amount, date, categoryId, paymentMethod, description);
        System.out.println("[SUCCESS] Expense record added successfully with ID: " + expense.getId());
    }

    private static void viewAllExpenses() throws Exception {
        List<Expense> expenses = expenseService.getAllExpenses();
        System.out.println("--- All Expense Records ---");
        if (expenses.isEmpty()) {
            System.out.println("No expense records found.");
        } else {
            expenses.forEach(System.out::println);
        }
    }

    private static void searchExpenseById(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Expense ID: ", 1);
        Expense expense = expenseService.getExpenseById(id);
        System.out.println("--- Expense Found ---");
        System.out.println(expense);
    }

    private static void updateExpense(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Expense ID to Update: ", 1);
        Expense existing = expenseService.getExpenseById(id);
        System.out.println("Current Record: " + existing);

        String title = InputValidator.readNonEmptyString(scanner, "Enter New Title [" + existing.getTitle() + "]: ");
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter New Amount [" + existing.getAmount() + "]: ");
        LocalDate date = InputValidator.readLocalDate(scanner, "Enter New Date [" + existing.getExpenseDate() + "]");
        displayCategories(CategoryType.EXPENSE);
        long categoryId = InputValidator.readLong(scanner, "Enter New Category ID [" + existing.getCategoryId() + "]: ", 1);
        PaymentMethod paymentMethod = InputValidator.readPaymentMethod(scanner, "Select New Payment Method:");
        String description = InputValidator.readOptionalString(scanner, "Enter New Description: ");

        boolean updated = expenseService.updateExpense(id, title, amount, date, categoryId, paymentMethod, description);
        if (updated) {
            System.out.println("[SUCCESS] Expense record updated successfully.");
        } else {
            System.out.println("[FAILED] Could not update expense record.");
        }
    }

    private static void deleteExpense(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Expense ID to Delete: ", 1);
        Expense existing = expenseService.getExpenseById(id);
        System.out.println("Record to delete: " + existing);
        String confirm = InputValidator.readNonEmptyString(scanner, "Are you sure you want to delete this record? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            boolean deleted = expenseService.deleteExpense(id);
            if (deleted) {
                System.out.println("[SUCCESS] Expense record deleted successfully.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void searchExpensesByCategory(Scanner scanner) throws Exception {
        displayCategories(CategoryType.EXPENSE);
        long categoryId = InputValidator.readLong(scanner, "Enter Expense Category ID: ", 1);
        List<Expense> expenses = expenseService.getExpensesByCategory(categoryId);
        System.out.println("--- Expense Records for Category ID " + categoryId + " ---");
        if (expenses.isEmpty()) {
            System.out.println("No expense records found for this category.");
        } else {
            expenses.forEach(System.out::println);
        }
    }

    private static void viewMonthlyExpenses(Scanner scanner) throws Exception {
        int month = InputValidator.readInt(scanner, "Enter Month (1-12): ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        BigDecimal total = expenseService.getTotalExpensesByMonth(month, year);
        System.out.printf("\n>>> Total Expenses for %02d/%d: $%.2f <<<\n", month, year, total);
    }

    private static void viewAnnualExpenses(Scanner scanner) throws Exception {
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        BigDecimal total = expenseService.getTotalExpensesByYear(year);
        System.out.printf("\n>>> Total Expenses for Year %d: $%.2f <<<\n", year, total);
    }

    private static void viewHighestExpenses(Scanner scanner) throws Exception {
        int limit = InputValidator.readInt(scanner, "How many top highest expenses to display? (1-50): ", 1, 50);
        List<Expense> expenses = expenseService.getHighestExpenses(limit);
        System.out.println("--- Top " + limit + " Highest Expenses ---");
        if (expenses.isEmpty()) {
            System.out.println("No expense records found.");
        } else {
            expenses.forEach(System.out::println);
        }
    }


    // ===================================================
    // 3. CATEGORY SUBMENU
    // ===================================================
    private static void handleCategorySubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- CATEGORY MANAGEMENT ---");
            System.out.println("1. Add Category");
            System.out.println("2. View Categories");
            System.out.println("3. Search Category");
            System.out.println("4. Update Category");
            System.out.println("5. Delete Category");
            System.out.println("6. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-6): ", 1, 6);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> addCategory(scanner);
                    case 2 -> viewCategoriesMenu(scanner);
                    case 3 -> searchCategoryById(scanner);
                    case 4 -> updateCategory(scanner);
                    case 5 -> deleteCategory(scanner);
                    case 6 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Category operation failed: " + e.getMessage());
            }
        }
    }

    private static void addCategory(Scanner scanner) throws Exception {
        System.out.println("--- Add New Category ---");
        String name = InputValidator.readNonEmptyString(scanner, "Enter Category Name: ");
        CategoryType type = InputValidator.readCategoryType(scanner, "Select Category Type");
        String description = InputValidator.readOptionalString(scanner, "Enter Description (Optional): ");

        Category category = categoryService.addCategory(name, type, description);
        System.out.println("[SUCCESS] Category created with ID: " + category.getId());
    }

    private static void viewCategoriesMenu(Scanner scanner) throws Exception {
        System.out.println("1. View All Categories");
        System.out.println("2. View Income Categories");
        System.out.println("3. View Expense Categories");
        int subChoice = InputValidator.readInt(scanner, "Select option (1-3): ", 1, 3);

        List<Category> categories;
        if (subChoice == 2) {
            categories = categoryService.getCategoriesByType(CategoryType.INCOME);
        } else if (subChoice == 3) {
            categories = categoryService.getCategoriesByType(CategoryType.EXPENSE);
        } else {
            categories = categoryService.getAllCategories();
        }

        System.out.println("--- Categories List ---");
        if (categories.isEmpty()) {
            System.out.println("No categories found.");
        } else {
            categories.forEach(System.out::println);
        }
    }

    private static void displayCategories(CategoryType type) throws Exception {
        List<Category> categories = categoryService.getCategoriesByType(type);
        System.out.println("\nAvailable " + type + " Categories:");
        categories.forEach(c -> System.out.printf("  ID: %-4d | Name: %-20s\n", c.getId(), c.getName()));
        System.out.println();
    }

    private static void searchCategoryById(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Category ID: ", 1);
        Category category = categoryService.getCategoryById(id);
        System.out.println("--- Category Found ---");
        System.out.println(category);
    }

    private static void updateCategory(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Category ID to Update: ", 1);
        Category existing = categoryService.getCategoryById(id);
        System.out.println("Current Category: " + existing);

        String name = InputValidator.readNonEmptyString(scanner, "Enter New Name [" + existing.getName() + "]: ");
        CategoryType type = InputValidator.readCategoryType(scanner, "Select New Type");
        String description = InputValidator.readOptionalString(scanner, "Enter New Description: ");

        boolean updated = categoryService.updateCategory(id, name, type, description);
        if (updated) {
            System.out.println("[SUCCESS] Category updated successfully.");
        }
    }

    private static void deleteCategory(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Category ID to Delete: ", 1);
        Category existing = categoryService.getCategoryById(id);
        System.out.println("Category to delete: " + existing);

        String confirm = InputValidator.readNonEmptyString(scanner, "Are you sure? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            boolean deleted = categoryService.deleteCategory(id);
            if (deleted) {
                System.out.println("[SUCCESS] Category deleted successfully.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }


    // ===================================================
    // 4. BUDGET SUBMENU
    // ===================================================
    private static void handleBudgetSubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- BUDGET MANAGEMENT ---");
            System.out.println("1. Create Budget");
            System.out.println("2. View All Budgets");
            System.out.println("3. Search Budget by ID");
            System.out.println("4. Update Budget");
            System.out.println("5. Delete Budget");
            System.out.println("6. Check Budget Status");
            System.out.println("7. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-7): ", 1, 7);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> createBudget(scanner);
                    case 2 -> viewAllBudgets();
                    case 3 -> searchBudgetById(scanner);
                    case 4 -> updateBudget(scanner);
                    case 5 -> deleteBudget(scanner);
                    case 6 -> checkBudgetStatus(scanner);
                    case 7 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Budget operation failed: " + e.getMessage());
            }
        }
    }

    private static void createBudget(Scanner scanner) throws Exception {
        System.out.println("--- Create Monthly Budget ---");
        displayCategories(CategoryType.EXPENSE);
        long categoryId = InputValidator.readLong(scanner, "Enter Expense Category ID: ", 1);
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter Monthly Budget Amount: ");
        int month = InputValidator.readInt(scanner, "Enter Month (1-12): ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);

        Budget budget = budgetService.setBudget(categoryId, amount, month, year);
        System.out.println("[SUCCESS] Monthly budget created successfully with ID: " + budget.getId());
    }

    private static void viewAllBudgets() throws Exception {
        List<Budget> budgets = budgetService.getAllBudgets();
        System.out.println("--- All Monthly Budgets ---");
        if (budgets.isEmpty()) {
            System.out.println("No budget records found.");
        } else {
            budgets.forEach(System.out::println);
        }
    }

    private static void searchBudgetById(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Budget ID: ", 1);
        BudgetSummary summary = budgetService.getBudgetStatus(id);
        printBudgetSummaryCard(summary);
    }

    private static void updateBudget(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Budget ID to Update: ", 1);
        Budget existing = budgetService.getBudgetById(id);
        System.out.println("Current Record: " + existing);

        displayCategories(CategoryType.EXPENSE);
        long categoryId = InputValidator.readLong(scanner, "Enter New Expense Category ID [" + existing.getCategoryId() + "]: ", 1);
        BigDecimal amount = InputValidator.readPositiveBigDecimal(scanner, "Enter New Budget Amount [" + existing.getBudgetAmount() + "]: ");
        int month = InputValidator.readInt(scanner, "Enter New Month (1-12) [" + existing.getMonth() + "]: ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter New Year [" + existing.getYear() + "]: ", 1900, 2100);

        boolean updated = budgetService.updateBudget(id, categoryId, amount, month, year);
        if (updated) {
            System.out.println("[SUCCESS] Budget record updated successfully.");
        }
    }

    private static void deleteBudget(Scanner scanner) throws Exception {
        long id = InputValidator.readLong(scanner, "Enter Budget ID to Delete: ", 1);
        Budget existing = budgetService.getBudgetById(id);
        System.out.println("Record to delete: " + existing);

        String confirm = InputValidator.readNonEmptyString(scanner, "Are you sure? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            boolean deleted = budgetService.deleteBudget(id);
            if (deleted) {
                System.out.println("[SUCCESS] Budget record deleted successfully.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void checkBudgetStatus(Scanner scanner) throws Exception {
        int month = InputValidator.readInt(scanner, "Enter Month (1-12): ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        List<BudgetSummary> summaries = budgetService.getBudgetsForMonthAndYear(month, year);

        System.out.printf("\n--- BUDGET STATUS FOR %02d/%d ---\n", month, year);
        if (summaries.isEmpty()) {
            System.out.println("No budgets defined for this month and year.");
        } else {
            summaries.forEach(Main::printBudgetSummaryCard);
        }
    }

    private static void printBudgetSummaryCard(BudgetSummary bs) {
        Budget b = bs.getBudget();
        System.out.println("--------------------------------------------------");
        System.out.printf("Category:       %-20s (ID: %d)\n", b.getCategoryName(), b.getCategoryId());
        System.out.printf("Month/Year:     %02d/%d\n", b.getMonth(), b.getYear());
        System.out.printf("Budget Limit:   $%.2f\n", b.getBudgetAmount());
        System.out.printf("Actual Spent:   $%.2f\n", bs.getActualExpenses());
        System.out.printf("Remaining:      $%.2f\n", bs.getRemainingBudget());
        System.out.printf("Usage %%:       %.2f%%\n", bs.getUsagePercentage());
        System.out.printf("Status:         [%s]\n", bs.getStatus());
        System.out.println("--------------------------------------------------");
    }


    // ===================================================
    // 5. FINANCIAL SUMMARY SUBMENU
    // ===================================================
    private static void handleFinancialSummarySubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- FINANCIAL SUMMARY & REPORTS ---");
            System.out.println("1. View Overall Financial Summary");
            System.out.println("2. View Monthly Summary");
            System.out.println("3. View Annual Summary");
            System.out.println("4. View Category-Wise Expenses");
            System.out.println("5. View Recent Transactions");
            System.out.println("6. View Savings");
            System.out.println("7. View Budget Utilization");
            System.out.println("8. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-8): ", 1, 8);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> viewOverallSummary();
                    case 2 -> viewMonthlySummary(scanner);
                    case 3 -> viewAnnualSummary(scanner);
                    case 4 -> viewCategoryWiseExpenses();
                    case 5 -> viewRecentTransactions(scanner);
                    case 6 -> viewSavings(scanner);
                    case 7 -> checkBudgetStatus(scanner);
                    case 8 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Summary operation failed: " + e.getMessage());
            }
        }
    }

    private static void viewOverallSummary() throws Exception {
        FinancialSummary summary = summaryService.getOverallSummary();
        System.out.println("==================================================");
        System.out.println("            OVERALL FINANCIAL SUMMARY             ");
        System.out.println("==================================================");
        System.out.printf(" Total Lifetime Income:   $%.2f\n", summary.getTotalIncome());
        System.out.printf(" Total Lifetime Expenses: $%.2f\n", summary.getTotalExpenses());
        System.out.printf(" Current Net Balance:     $%.2f\n", summary.getNetBalanceOrSavings());
        System.out.println("==================================================");
    }

    private static void viewMonthlySummary(Scanner scanner) throws Exception {
        int month = InputValidator.readInt(scanner, "Enter Month (1-12): ", 1, 12);
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        FinancialSummary summary = summaryService.getMonthlySummary(month, year);
        System.out.println("==================================================");
        System.out.printf("         MONTHLY SUMMARY FOR %02d/%d             \n", month, year);
        System.out.println("==================================================");
        System.out.printf(" Total Monthly Income:   $%.2f\n", summary.getTotalIncome());
        System.out.printf(" Total Monthly Expenses: $%.2f\n", summary.getTotalExpenses());
        System.out.printf(" Monthly Net Savings:    $%.2f\n", summary.getNetBalanceOrSavings());
        System.out.println("==================================================");
    }

    private static void viewAnnualSummary(Scanner scanner) throws Exception {
        int year = InputValidator.readInt(scanner, "Enter Year (e.g. 2026): ", 1900, 2100);
        FinancialSummary summary = summaryService.getAnnualSummary(year);
        System.out.println("==================================================");
        System.out.printf("           ANNUAL SUMMARY FOR YEAR %d             \n", year);
        System.out.println("==================================================");
        System.out.printf(" Total Annual Income:   $%.2f\n", summary.getTotalIncome());
        System.out.printf(" Total Annual Expenses: $%.2f\n", summary.getTotalExpenses());
        System.out.printf(" Annual Net Savings:    $%.2f\n", summary.getNetBalanceOrSavings());
        System.out.println("==================================================");
    }

    private static void viewCategoryWiseExpenses() throws Exception {
        Map<String, BigDecimal> catExpenses = summaryService.getCategoryWiseExpenses();
        System.out.println("--- Category-Wise Expense Totals ---");
        if (catExpenses.isEmpty()) {
            System.out.println("No expenses logged yet.");
        } else {
            catExpenses.forEach((cat, total) -> System.out.printf(" Category: %-22s | Total Spent: $%.2f\n", cat, total));
        }
    }

    private static void viewRecentTransactions(Scanner scanner) throws Exception {
        int limit = InputValidator.readInt(scanner, "Number of recent transactions to view (1-50): ", 1, 50);
        List<SummaryService.RecentTransaction> list = summaryService.getRecentTransactions(limit);
        System.out.println("--- Recent Transactions ---");
        if (list.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            list.forEach(System.out::println);
        }
    }

    private static void viewSavings(Scanner scanner) throws Exception {
        System.out.println("1. Calculate Monthly Savings");
        System.out.println("2. Calculate Annual Savings");
        int subChoice = InputValidator.readInt(scanner, "Select option (1-2): ", 1, 2);
        if (subChoice == 1) {
            viewMonthlySummary(scanner);
        } else {
            viewAnnualSummary(scanner);
        }
    }


    // ===================================================
    // 6. SEARCH TRANSACTIONS SUBMENU
    // ===================================================
    private static void handleSearchTransactionsSubmenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- SEARCH TRANSACTIONS ---");
            System.out.println("1. Search Income by Date Range");
            System.out.println("2. Search Expense by Date Range");
            System.out.println("3. Search Income by Category");
            System.out.println("4. Search Expenses by Category");
            System.out.println("5. Back to Main Menu");

            int choice = InputValidator.readInt(scanner, "Select option (1-5): ", 1, 5);
            System.out.println();

            try {
                switch (choice) {
                    case 1 -> {
                        LocalDate start = InputValidator.readLocalDate(scanner, "Start Date");
                        LocalDate end = InputValidator.readLocalDate(scanner, "End Date");
                        List<Income> incomes = incomeService.getIncomeByDateRange(start, end);
                        System.out.printf("--- Income Records (%s to %s) ---\n", start, end);
                        if (incomes.isEmpty()) System.out.println("No matching income records.");
                        else incomes.forEach(System.out::println);
                    }
                    case 2 -> {
                        LocalDate start = InputValidator.readLocalDate(scanner, "Start Date");
                        LocalDate end = InputValidator.readLocalDate(scanner, "End Date");
                        List<Expense> expenses = expenseService.getExpensesByDateRange(start, end);
                        System.out.printf("--- Expense Records (%s to %s) ---\n", start, end);
                        if (expenses.isEmpty()) System.out.println("No matching expense records.");
                        else expenses.forEach(System.out::println);
                    }
                    case 3 -> searchIncomeByCategory(scanner);
                    case 4 -> searchExpensesByCategory(scanner);
                    case 5 -> back = true;
                }
            } catch (Exception e) {
                System.out.println("[!] Search failed: " + e.getMessage());
            }
        }
    }
}
