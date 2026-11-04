# Architectural & Engineering Decisions Log (DECISIONS.md)

**Project:** Artha - Native Android Offline-First Money & Wealth Tracker  
**Package:** `com.artha.money`  
**Date:** 4 Nov 2026  
**Author:** @Lokesh / Senior Android Engineer & Product Designer

---

## 1. App Naming & Identity
- **Decision:** The application is named **Artha** (Sanskrit for *Wealth, Meaning, Purpose, Prosperity*).
- **Rationale:** Reflects a calm, purposeful, premium, and holistic financial tracking and wealth-building experience.

---

## 2. Technology Stack & Multi-Module Architecture
- **Decision:** Pure Kotlin 2.x, Jetpack Compose, Material 3 with dynamic color tokens, Room SQLite in WAL mode with SQLCipher, DataStore, Hilt DI, Coroutines & Flow. No Flutter, React Native, or webviews.
- **Module Layout:**
  - `:app`: Application entry point, single Activity, Navigation Compose host, Hilt dependency root, Jetpack Glance home screen widgets.
  - `:core:model`: Pure Kotlin domain model, `Money` value class, domain enums, currency metadata (zero Android SDK dependencies).
  - `:core:database`: Room Database, SQLCipher encryption, Room DAOs, Entities, TypeConverters, FTS5 virtual table, and incremental aggregate management.
  - `:core:domain`: Business logic, use cases, rules engine, single-count credit card validation, loan amortization calculators, safe-to-spend algorithms, natural-language parser.
  - `:core:data`: Repository implementations, WorkManager tasks, SAF (Storage Access Framework) backup, Keystore crypto.
  - `:core:ui`: Theme tokens, `CompositionLocal` design system, custom math keypad (`120+45*2`), Canvas charts, squircle icons, localized currency formatters.
  - `:feature:dashboard`, `:feature:transactions`, `:feature:accounts`, `:feature:budgets`, `:feature:reports`, `:feature:customization`: UI feature slices.

---

## 3. Money Representation & Arithmetic Math
- **Decision:** Every monetary amount is stored as a 64-bit integer (`Long`) in minor currency units (e.g. 10000 = $100.00 / ₹100.00). Floating-point `Float` and `Double` are strictly prohibited for monetary calculations.
- **Decision:** Arithmetic operations are encapsulated in `Money` value/data class:
  - Strict Banker's Rounding (`RoundingMode.HALF_EVEN`) when multiplying or scaling.
  - **Zero-Loss Split Distribution:** Splitting an amount into $N$ parts divides $amount / N$ and distributes the remainder 1 minor unit at a time to the first $R$ buckets, guaranteeing the sum of splits exactly equals the original amount.
  - **Locale-Aware Formatting:** Supports Indian numbering (e.g., Lakhs and Crores: `₹1,50,000.00`) and International grouping (e.g., `USD 150,000.00`), alongside currency symbols.

---

## 4. Single-Count Credit Card & Payment Method Invariant
- **Decision:** Enforce the single-count credit card rule strictly in domain logic:
  - An expense on a credit card raises the credit card liability and counts as an expense immediately on the purchase date.
  - Paying a credit card bill is recorded as a `TRANSFER` from an asset account (bank/checking) to the credit card liability account.
  - Transfers are excluded from all spending reports, expense categories, budget consumptions, and widgets.
  - Net worth decreases at the moment of purchase, not at the time of bill payment.

---

## 5. Incremental Aggregate Tables for Instant Dashboards
- **Decision:** The database maintains summary tables (`monthly_aggregates`, `category_monthly_aggregates`, `account_monthly_aggregates`) synchronized within the same database transaction as transaction writes.
- **Rationale:** Guarantees sub-50ms dashboard render times on mid-range hardware even when 100,000+ historical transactions are recorded, eliminating raw table full scans.

---

## 6. Privacy & Security
- **Decision:** 100% offline-first. Zero analytics SDKs, zero third-party ads, zero tracking.
- **Decision:** Database encryption powered by SQLCipher, using a key securely generated and stored in Android Keystore (`AndroidKeyStore`).
- **Decision:** App lock with BiometricPrompt / PIN, secure window flag (`FLAG_SECURE`) to block screenshots and recents preview when enabled.

---

## 7. Dynamic User-Editable Theme System
- **Decision:** Design tokens (colors, font scaling, density, corner radii) are exposed via Compose `CompositionLocal` and persisted in DataStore.
- **Rationale:** Allows live editing with real-time UI updates without requiring an application restart.

---

## 8. Milestone 2: Core Ledger & Account Architecture
- **Decision: In-App Math Expression Evaluator (`MathExpressionEvaluator`)**:
  - The fast-add keypad allows real-time evaluation of simple expressions (e.g., `120+45*2` = 210) directly inside the bottom sheet without opening an external calculator.
  - Implements an $O(N)$ Shunting-Yard operator precedence evaluator with live preview while typing.
- **Decision: Thumb-Zone Fast Add Sheet (`FastAddTransactionSheet`)**:
  - Located in the bottom half of the screen for one-handed operation.
  - 3-tap rapid entry flow: enter amount -> select category pill -> tap Checkmark.
  - Atomic database write guarantees <50ms commit time and immediate UI dismissal without a loading spinner.
- **Decision: Reversible Deletions with Undo Buffer**:
  - When a transaction is deleted, `TransactionDao.deleteTransactionWithAggregates` atomically reverts the source/target account balances and deducts the expense from the monthly and category summary aggregate tables.
  - Deleted entity is retained in an undo buffer for 4 seconds; tapping "Undo" re-inserts the entity atomically, ensuring aggregate consistency.
- **Decision: Hand-Tick Statement Reconciliation Mode (`ReconcileScreen`)**:
  - Reconcile mode tracks an account's running reconciled balance against a user-entered bank statement ending balance.
  - One-tap toggle updates `is_reconciled` on individual transactions and displays the live difference.
  - When difference equals zero, the screen alerts "Balanced!", allowing users to verify their ledgers against real-world bank statements.
- **Decision: First-Launch Zero-Friction Seeding**:
  - To prevent empty-state paralysis, `ArthaApp` asynchronously seeds realistic starter categories (Food, Groceries, Shopping, Transport, Bills, Salary, etc.) and accounts (Bank, Cash, Credit Card) on first launch.

---

## 9. Milestone 3: Planning, Budgets, Subscriptions & WorkManager
- **Decision: Safe-To-Spend Daily Allowance Engine (`BudgetEngine`)**:
  - Automatically calculates remaining envelope allowances divided by days remaining in the billing period.
  - Returns positive real-time daily spending limits and gracefully handles over-budget states (displaying 0 allowance with alert badge).
  - Supports configurable alert thresholds (e.g., warning at 80% and alarm at 100%).
- **Decision: Subscription Annualization & Rollup (`SubscriptionCalculator`)**:
  - Standardizes irregular billing frequencies (Daily, Weekly, Monthly, Quarterly, Yearly) into a unified Annual Cost Rollup and Monthly Equivalent.
  - Automatically checks renewal proximities (warning at 7 days) and impending free trial expirations (warning at 3 days) with direct deep-links to cancellation URLs.
- **Decision: Background Processing with WorkManager (`RecurringTransactionWorker`)**:
  - Uses AndroidX `WorkManager` with `ExistingPeriodicWorkPolicy.KEEP` running a daily background check.
  - When auto-apply recurring rules are due, automatically commits the ledger transaction and advances the rule's next due date via `RecurringScheduleCalculator`.
- **Decision: Sinking Funds & Savings Goals**:
  - Tracked as progress envelopes towards a future target date.
  - Provides instant 1-tap contribution buttons to accelerate wealth accumulation.


