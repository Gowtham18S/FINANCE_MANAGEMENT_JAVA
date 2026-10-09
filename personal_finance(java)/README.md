# Personal Finance Management System

A production-ready, menu-driven **Personal Finance Management System** built using **Pure Core Java**, **JDBC (Java Database Connectivity)**, and **MySQL Database**.

> [!NOTE]
> **Zero Framework Architecture**: This project strictly adheres to pure Core Java principles. It uses **NO** Spring Boot, **NO** ORM (Hibernate/JPA), **NO** Web Servers/REST APIs, and **NO** Frontend/GUI frameworks. Data persistence is powered directly by MySQL 8.0+ via standard JDBC `PreparedStatement` and `Connection` pools.

---

## Table of Contents
1. [Key Features](#key-features)
2. [Technology Stack](#technology-stack)
3. [Project Directory Structure](#project-directory-structure)
4. [Database Configuration & Schema](#database-configuration--schema)
5. [Installation & Setup Guide (Windows & IntelliJ IDEA)](#installation--setup-guide-windows--intellij-idea)
6. [Running the Application](#running-the-application)
7. [Console Interface Menu Sitemap](#console-interface-menu-sitemap)
8. [Automated Testing](#automated-testing)
9. [MySQL Data Verification Commands](#mysql-data-verification-commands)
10. [College Viva & Academic Concept Guide](#college-viva--academic-concept-guide)

---

## Key Features

- 💰 **Income Management (CRUD)**: Add, view, update, delete, search by category/date range, calculate monthly and annual income.
- 💸 **Expense Management (CRUD)**: Add, view, update, delete, payment method categorization (`CASH`, `UPI`, `DEBIT_CARD`, `CREDIT_CARD`, `BANK_TRANSFER`, `OTHER`), search by category/date range, calculate monthly/annual expenses, top highest expenses report.
- 🏷️ **Category Management**: Organize income and expense categories, prevent duplicate names per type, delete protection for categories linked to active transactions or budgets.
- 📊 **Monthly Budget Management**: Set category-specific monthly spending limits, track live actual spend from transactions, calculate remaining allowance, calculate percentage utilization, and trigger limit status warnings (`WITHIN LIMIT` vs `EXCEEDED`).
- 📈 **Financial Summaries & Metrics**: Compute total net balance, monthly savings, annual savings, category-wise expenditure breakdowns, recent combined transactions timeline, and monthly budget utilization cards using high-precision `BigDecimal`.
- 🛡️ **Robust Validation & Error Resilience**: Auto-initializes MySQL database and missing tables on startup without data loss, handles invalid menu selections and numeric input errors gracefully without application crashes.

---

## Technology Stack

- **Programming Language**: Java 17 / Java 21 / Java 25 (Standard Edition)
- **Database Engine**: MySQL 8.0+
- **Database Connectivity**: JDBC (`java.sql.*`)
- **Database Driver**: MySQL Connector/J (`com.mysql:mysql-connector-j:8.3.0`)
- **Build Tool**: Apache Maven (Dependency management and execution)
- **IDE Support**: IntelliJ IDEA / Eclipse / VS Code

---

## Project Directory Structure

```
PersonalFinanceManagementSystem/
│
├── pom.xml                                   # Maven dependencies & build configurations
├── database.sql                              # Database creation DDL & seed data script
├── README.md                                 # Complete documentation & viva guide
│
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── personalfinance/
    │               ├── Main.java             # Entry point & interactive console menu
    │               │
    │               ├── config/
    │               │   └── DatabaseConnection.java    # JDBC connection manager & diagnostics
    │               │
    │               ├── model/
    │               │   ├── Category.java            # Category entity
    │               │   ├── CategoryType.java        # INCOME / EXPENSE Enum
    │               │   ├── Income.java              # Income entity
    │               │   ├── Expense.java             # Expense entity
    │               │   ├── PaymentMethod.java       # Payment method Enum
    │               │   ├── Budget.java              # Budget entity
    │               │   ├── BudgetSummary.java       # Budget status reporting model
    │               │   └── FinancialSummary.java    # Summary metrics value object
    │               │
    │               ├── dao/
    │               │   ├── CategoryDAO.java         # JDBC operations for categories
    │               │   ├── IncomeDAO.java           # JDBC operations for income
    │               │   ├── ExpenseDAO.java          # JDBC operations for expenses
    │               │   └── BudgetDAO.java           # JDBC operations for budgets
    │               │
    │               ├── service/
    │               │   ├── CategoryService.java     # Category business logic & validation
    │               │   ├── IncomeService.java       # Income business logic & validation
    │               │   ├── ExpenseService.java      # Expense business logic & validation
    │               │   ├── BudgetService.java       # Budget tracking & usage calculations
    │               │   └── SummaryService.java      # Financial reports & savings metrics
    │               │
    │               └── util/
    │                   ├── InputValidator.java      # Console input parser & exception guard
    │                   └── DatabaseInitializer.java # Auto database & table bootstrapper
    │
    └── test/
        └── java/
            └── com/
                └── personalfinance/
                    ├── service/                 # Comprehensive unit tests for services
                    └── integration/             # Live MySQL connection integration tests
```

---

## Database Configuration & Schema

The application automatically connects to MySQL and bootstraps the `personal_finance_db` database and required tables upon startup.

### Schema Details (`database.sql`):
1. `categories`: ID, Name, Type (`INCOME` / `EXPENSE`), Description (UNIQUE on Name + Type).
2. `income`: ID, Source, Amount (`DECIMAL(15,2)`), Date, Category ID (FK), Description, Created Timestamp.
3. `expenses`: ID, Title, Amount (`DECIMAL(15,2)`), Date, Category ID (FK), Payment Method, Description, Created Timestamp.
4. `budgets`: ID, Category ID (FK), Budget Amount (`DECIMAL(15,2)`), Month (1-12), Year (UNIQUE on Category + Month + Year).

---

## Installation & Setup Guide (Windows & IntelliJ IDEA)

### Step 1: Verify Java Installation
Open Command Prompt (`cmd`) or PowerShell and verify Java 17+:
```cmd
java -version
```

### Step 2: Verify MySQL Installation
Ensure MySQL Server 8.0+ service is running on `localhost:3306`:
```powershell
Test-NetConnection -ComputerName localhost -Port 3306
```

### Step 3: Configure Database Credentials on Windows
You can set environment variables for custom MySQL username and password.

#### Option A: Command Prompt (`cmd`)
```cmd
set DB_URL=jdbc:mysql://localhost:3306/personal_finance_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
set DB_USER=root
set DB_PASSWORD=your_mysql_password
```

#### Option B: PowerShell
```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="your_mysql_password"
```

#### Option C: System Environment Variables (Permanent)
1. Press `Win + R`, type `sysdm.cpl`, press Enter.
2. Go to **Advanced** tab -> **Environment Variables**.
3. Under User or System variables, click **New...**:
   - Variable name: `DB_USER` | Variable value: `root`
   - Variable name: `DB_PASSWORD` | Variable value: `your_mysql_password`

---

## Running the Application

### Option 1: Via Maven Command Line
Navigate to project directory and run:
```cmd
mvn exec:java
```

### Option 2: Running in IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Select **File -> Open...** and choose the `PersonalFinanceManagementSystem` project folder.
3. IntelliJ will detect `pom.xml` and import dependencies automatically.
4. Navigate to `src/main/java/com/personalfinance/Main.java`.
5. Right-click `Main.java` and select **Run 'Main.main()'**.
6. *(Optional)* If your MySQL requires a password, configure it under **Run -> Edit Configurations... -> Environment Variables**: `DB_USER=root;DB_PASSWORD=your_password`.

---

## Console Interface Menu Sitemap

```
========================================
   PERSONAL FINANCE MANAGEMENT SYSTEM   
========================================
1. Income Management
   ├── 1. Add Income
   ├── 2. View All Income
   ├── 3. Search Income by ID
   ├── 4. Update Income
   ├── 5. Delete Income
   ├── 6. Search Income by Category
   ├── 7. View Monthly Income
   ├── 8. View Annual Income
   └── 9. Back to Main Menu

2. Expense Management
   ├── 1. Add Expense
   ├── 2. View All Expenses
   ├── 3. Search Expense by ID
   ├── 4. Update Expense
   ├── 5. Delete Expense
   ├── 6. Search Expenses by Category
   ├── 7. View Monthly Expenses
   ├── 8. View Annual Expenses
   ├── 9. View Highest Expenses
   └── 10. Back to Main Menu

3. Category Management
   ├── 1. Add Category
   ├── 2. View Categories (All / Income / Expense)
   ├── 3. Search Category by ID
   ├── 4. Update Category
   ├── 5. Delete Category (With FK reference guard)
   └── 6. Back to Main Menu

4. Budget Management
   ├── 1. Create Monthly Budget
   ├── 2. View All Budgets
   ├── 3. Search Budget by ID
   ├── 4. Update Budget
   ├── 5. Delete Budget
   ├── 6. Check Budget Status & Remaining Allowance
   └── 7. Back to Main Menu

5. Financial Summary & Reports
   ├── 1. View Overall Financial Summary (Lifetime Balance)
   ├── 2. View Monthly Summary
   ├── 3. View Annual Summary
   ├── 4. View Category-Wise Expenses
   ├── 5. View Recent Transactions Timeline
   ├── 6. View Monthly/Annual Net Savings
   ├── 7. View Monthly Budget Utilization
   └── 8. Back to Main Menu

6. Search Transactions
   ├── 1. Search Income by Date Range
   ├── 2. Search Expense by Date Range
   ├── 3. Search Income by Category
   └── 4. Search Expenses by Category

7. Exit
```

---

## Automated Testing

To run unit tests across all business services (`CategoryServiceTest`, `IncomeServiceTest`, `ExpenseServiceTest`, `BudgetServiceTest`, `SummaryServiceTest`):
```cmd
mvn test
```

---

## MySQL Data Verification Commands

To directly inspect and verify data stored in MySQL Workbench or MySQL CLI:

```sql
USE personal_finance_db;

-- List all tables
SHOW TABLES;

-- Inspect Categories
SELECT * FROM categories ORDER BY type, name;

-- Inspect Income Records
SELECT i.id, i.source, i.amount, i.income_date, c.name AS category
FROM income i
JOIN categories c ON i.category_id = c.id;

-- Inspect Expense Records
SELECT e.id, e.title, e.amount, e.expense_date, e.payment_method, c.name AS category
FROM expenses e
JOIN categories c ON e.category_id = c.id;

-- Inspect Monthly Budgets
SELECT b.id, c.name AS category, b.budget_amount, b.month, b.year
FROM budgets b
JOIN categories c ON b.category_id = c.id;
```

---

## College Viva & Academic Concept Guide

When presenting this project for your college viva or project presentation, use the following explanations to demonstrate deep understanding of Core Java and Database architecture:

### 1. How Java Connects to MySQL
Java connects to MySQL using **JDBC (Java Database Connectivity)** API provided by Java Standard Library (`java.sql`).
- The application loads the MySQL Connector/J driver class (`com.mysql.cj.jdbc.Driver`).
- The connection is established via `DriverManager.getConnection(url, user, password)`.
- The `DatabaseConnection` utility centralizes connection creation using environment variables to keep credentials secure.

### 2. What JDBC Does
JDBC acts as a standard bridge between Java code and relational database engines.
- **Connection**: Manages session state with MySQL.
- **PreparedStatement**: Compiles SQL queries on the database server in advance and binds user inputs safely using placeholders (`?`), completely preventing **SQL Injection attacks**.
- **ResultSet**: Represents tabular query results returned from MySQL, traversed using `rs.next()`.
- **Try-With-Resources**: Auto-closes JDBC connections, statements, and result sets to prevent database resource leaks.

### 3. DAO Pattern (Data Access Object)
The DAO pattern separates database access operations from business application logic.
- `CategoryDAO`, `IncomeDAO`, `ExpenseDAO`, and `BudgetDAO` execute SQL queries (`SELECT`, `INSERT`, `UPDATE`, `DELETE`) and map relational database rows into Java Model objects.

### 4. Service Layer Pattern
The Service layer contains core business logic, calculations, and validation rules.
- Example: `IncomeService` validates that amount is positive and that the selected category is strictly of type `INCOME` before sending data to `IncomeDAO`.
- `BudgetService` dynamically queries the `expenses` table to compute actual spent, remaining budget, and percentage usage without redundantly storing calculated values.

### 5. Financial Formulas Implemented
All monetary values use Java's `BigDecimal` to prevent binary floating-point rounding errors (inherent in `float` and `double`).
- **Total Income** = $\sum \text{Income Amounts}$
- **Total Expenses** = $\sum \text{Expense Amounts}$
- **Current Balance** = $\text{Total Income} - \text{Total Expenses}$
- **Monthly Savings** = $\text{Monthly Income} - \text{Monthly Expenses}$
- **Remaining Budget** = $\text{Budget Amount} - \text{Actual Category Expenses for Month}$
- **Budget Usage Percentage** = $(\frac{\text{Actual Expenses}}{\text{Budget Amount}}) \times 100$
