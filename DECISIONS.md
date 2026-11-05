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

---

## 10. Milestone 4: Insight, Cash Flow, EMI & Prepayment Simulation
- **Decision: Reducing-Balance Loan Amortization (`EmiCalculator`)**:
  - Implements the mathematical loan EMI formula with 64-bit integer minor math and Banker's Rounding:
    $EMI = \frac{P \cdot r \cdot (1 + r)^n}{(1 + r)^n - 1}$
  - Month-by-month schedule rigorously calculates interest and principal components, guaranteeing closing balance reaches exactly zero in the final month.
- **Decision: Prepayment Simulation Engine**:
  - Simulates the compounding financial impact of extra recurring monthly payments or lumpsum prepayments.
  - Dynamically calculates the exact total interest savings and the number of months shaved off the loan tenure.
- **Decision: Offline Cash Flow & Category Share (`ReportsEngine`)**:
  - Automatically aggregates inflow, outflow, net savings, and savings rate percentage ($(\text{Inflow} - \text{Outflow}) / \text{Inflow} \times 100$).
  - Produces category share distributions without querying raw transactions, using pre-computed Room aggregate tables.
- **Decision: Unified Shared Debts & IOUs**:
  - Segregates money lent (personal assets owed to user) from money borrowed and EMI loans (liabilities).
  - Offers instant 1-tap "Settle Up" action to record repayments and adjust debt balances.

---

## 11. Milestone 5: Standout Features (Sentence Parser, What-If Simulation & Rules Engine)
- **Decision: Natural Language Sentence Parser (`SentenceParser`)**:
  - Parses freeform English financial sentences (e.g. `"45 coffee at blue tokai card"`, `"₹250 groceries at nature basket upi"`, `"salary 85000 bank"`) into structured transaction properties: amount in minor units (supporting `k`, `L`/`lakh`, `cr`/`crore`), currency, transaction type, category, merchant, payment method, and relative dates (`yesterday`, `today`).
  - Integrated directly into `FastAddTransactionSheet` with real-time text reactive evaluation.
- **Decision: Multi-Year What-If Scenario Simulation (`WhatIfEngine`)**:
  - Projects financial balance sheets over 12, 36, and 60 months with compound investment growth ($r = (1 + R)^{1/12} - 1$).
  - Compares status-quo baseline vs simulated scenarios: one-time purchases with debt financing (e.g. "Buy a ₹15L Car"), subscription cancellations, SIP increases, and salary hikes.
  - Calculates milestone net worth deltas ($\Delta NW$), monthly cash flow impacts, liquid emergency runway months, and breakeven crossover points.
- **Decision: Smart Rules Engine (`RulesEngine` & `RuleRepository`)**:
  - Evaluates configurable conditions (`MERCHANT`, `AMOUNT`, `NOTE`, `PAYMENT_METHOD`) using operators (`CONTAINS`, `EQUALS`, `GREATER_THAN`, `LESS_THAN`, `REGEX`) and executes automated actions (`SET_CATEGORY`, `SET_PAYMENT_METHOD`, `EXCLUDE_FROM_BUDGET`, `EXCLUDE_FROM_REPORTS`).
  - Auto-evaluates on new transactions upon insertion and supports historical batch re-running with automatic incremental category aggregate synchronization.

---

## 12. Milestone 6: Offline Receipts, Data Portability & Bank CSV Engine
- **Decision: Zero-Cloud Receipt Attachment Management (`ReceiptManager`)**:
  - Stores captured receipt photos strictly in internal app-private scoped storage (`context.filesDir/receipts/`) with zero external cloud sync or public storage exposure.
- **Decision: On-Device Receipt Text OCR Analyzer (`ReceiptOcrExtractor`)**:
  - Heuristic offline regex extraction for receipt images: recognizes merchant brand, date patterns (`dd/MM/yyyy`, `yyyy-MM-dd`), tax components (GST, VAT), subtotal vs grand total amounts, and itemized line items.
- **Decision: Bank CSV Auto-Detection & Data Portability (`DataTransferEngine` & `DataTransferRepository`)**:
  - Exports transactions into standardized CSV and full encrypted JSON formats.
  - Automatically identifies arbitrary bank statement column headers (HDFC, ICICI, SBI, Chase, Standard Chartered) for Transaction Date, Description/Narration, Debit/Outflow, Credit/Inflow, and Balance, enabling instant offline bank statement imports.

---

## 13. Milestone 7: Ownership, Security, Home Widgets & Release Verification
- **Decision: Biometric Authentication Lock Screen (`BiometricPrompt`)**:
  - Protects the application behind `androidx.biometric.BiometricPrompt` supporting Class 3 Strong Biometrics (Fingerprint / Face) with fallback to Device PIN / Pattern / Password.
  - Displays a dedicated `AppLockScreen` overlay until authenticated on launch or resume when biometric protection is enabled.
- **Decision: Dynamic Screen & Recents Protection (`FLAG_SECURE`)**:
  - Dynamically attaches `WindowManager.LayoutParams.FLAG_SECURE` to window attributes when `isHideBalancesInRecents` is toggled on, preventing screen capture and blanking out the recents preview card in the Android system task switcher.
- **Decision: Home Screen Quick Glance Widget (`ArthaQuickWidgetProvider`)**:
  - Native `AppWidgetProvider` exposing Today's Spending, Daily Safe-to-Spend Allowance, and a 1-tap `+ Quick Add` launcher directly into the thumb-zone Fast Add sheet.
- **Decision: Release Verification**:
  - Verified with Room, SQLCipher, and Compose Proguard rules via `./gradlew assembleRelease` and `./gradlew assembleDebug`.

---

## 14. Device Compatibility & Startup Crash Resolution
- **Root Cause 1: Theme Mismatch with `FragmentActivity`**:
  - `MainActivity` extended `FragmentActivity` to host `BiometricPrompt`.
  - Android Manifest declared `@android:style/Theme.Material.NoActionBar`, which is not an `AppCompat` descendant. When `FragmentActivity` and AppCompat delegates initialized on app start, Android threw `IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.`
  - **Fix:** Added `Theme.Artha` extending `Theme.AppCompat.DayNight.NoActionBar` with transparent edge-to-edge system bars in `res/values/themes.xml` and `res/values-night/themes.xml`, and configured in `AndroidManifest.xml`.
- **Root Cause 2: Android KeyStore and `EncryptedSharedPreferences` on OEM Devices**:
  - `DatabaseKeyManager` initialized `MasterKey` and `EncryptedSharedPreferences` eagerly at property declaration time, using `java.util.Base64`.
  - On OEM ROMs (POCO, Xiaomi MIUI/HyperOS, Samsung, custom ROMs), KeyStore initialization can throw `KeyStoreException` or `GeneralSecurityException`.
  - **Fix:** Switched to `android.util.Base64` and made preferences lazy with a resilient `try-catch` fallback to sandboxed app-private `Context.MODE_PRIVATE` SharedPreferences.
- **Root Cause 3: Room Database Initialization Fallback**:
  - Added safety guard in `ArthaDatabase.getInstance()`: if SQLCipher native `.so` library or KeyStore fails for any device architecture reasons, it logs diagnostics and automatically falls back to standard Room SQLite so the application never crashes on launch.
- **Root Cause 4: WorkManager and Application Startup Protection**:
  - Shielded `WorkManager.getInstance()` and asynchronous initial data seeding inside `ArthaApp.onCreate()` with comprehensive exception handlers and installed a global `Thread.setDefaultUncaughtExceptionHandler` for logcat diagnostics.
- **Root Cause 5: Launcher Adaptive Icons**:
  - Replaced the platform system drawable `@android:drawable/sym_def_app_icon` with a dedicated adaptive vector icon (`ic_launcher.xml`, `ic_launcher_round.xml`, `ic_launcher_background.xml`, `ic_launcher_foreground.xml`) to prevent OEM launcher parsing errors.
- **Root Cause 6: Kotlin Initialization Order NullPointerException in `InsightViewModel`**:
  - `InsightViewModel` invoked `recomputeWhatIf()` inside its `init` block at line 125.
  - However, `_selectedScenario` and `_whatIfResult` were defined as properties at lines 221-225 (lexically below the `init` block). In Kotlin JVM bytecode, properties defined below `init` are initialized after `init` completes, leaving `_selectedScenario` as `null` during `init`.
  - Calling `_selectedScenario.value` threw `java.lang.NullPointerException: Attempt to invoke interface method 'java.lang.Object kotlinx.coroutines.flow.MutableStateFlow.getValue()' on a null object reference`.
  - **Fix:** Reordered all property definitions to precede the `init` block, wrapped init calculations in a `try-catch`, and added safety exception handling in `recomputeWhatIf()`.

---

## 15. Milestone 9: Cloud Backup, Currency Chooser, Transaction Editing, Custom Labels & Visual Category Icons
- **Decision: Google Account & Cloud Backup Engine (`SettingsScreen` & `UserPreferences`)**:
  - Integrated Google Account linking and automated encrypted Google Drive backups in Settings.
  - Implemented 1-tap "Backup to Drive" triggering Android `ACTION_SEND` targeting Google Drive/Files with `application/json`, updating backup timestamps and providing full backup restoration dialogs.
- **Decision: Global & Per-Transaction Currency Chooser (`CurrencyConfig` & `CurrencyPickerDialog`)**:
  - Expanded `CurrencyConfig` to support all major global currencies (`INR`, `USD`, `EUR`, `GBP`, `AED`, `SGD`, `CAD`, `AUD`, `JPY`, `CHF`, `SAR`, `CNY`, `KWD`).
  - Added primary currency chooser in Settings and interactive currency selector chip in transaction creation/editing sheets.
- **Decision: In-Place Transaction Editing (`EditTransactionSheet` & `TransactionRepository.updateTransaction`)**:
  - Added `EditTransactionSheet` allowing users to edit amount, date, type, category, account, payment method, note, and custom tags.
  - `updateTransaction` atomically deletes previous aggregates and reapplies new transaction aggregates in Room WAL SQLite, preventing drift.
- **Decision: Custom Labels / Tags Engine (`#tags` & Tag Filter Bar)**:
  - Enabled custom hashtag extraction and dynamic label management (`#Personal`, `#Work`, `#Vacation`, `#TaxSaver`, `#Family`, `#Reimbursable`, plus inline custom tag creation).
  - Added interactive tag filter chips to the transaction list header and visible label badges on each `TransactionCard`.
- **Decision: High-Contrast Category Visual Icons (`CategoryVisuals`)**:
  - Built `CategoryVisuals` mapping category entities and keywords to distinct vibrant icons (Restaurant 🍽️, Grocery 🛒, Shopping 🛍️, Car 🚗, Bills ⚡, Rent 🏠, Movie 🎬, Healthcare 🏥, Salary 💰, Investments 📈) and color-coded squircle containers.

---

## 16. Milestone 10: Real Native Google Account Chooser & SAF Google Drive Storage Integration
- **Context & User Feedback:**
  - Users expected standard Google Sign-In behavior (system account selection sheet) rather than manual text field entry, and true file-based Google Drive backup/restore.
- **Architectural Solution:**
  1. **Dual Google Sign-In Strategy:**
     - **Native OS Account Picker (`AccountManager.newChooseAccountIntent`)**: Invokes Android's native system Google account picker (`allowableAccountTypes = arrayOf("com.google")`). Displays real device Google accounts (`@gmail.com`) in the system modal sheet with zero cloud setup, zero API keys, and zero configuration errors.
     - **Google Play Services Auth (`play-services-auth:21.0.0`)**: Integrated `GoogleSignInClient` for users who wish to supply their Google Cloud OAuth Web Client ID.
     - **OAuth Credentials & Diagnostic Modal (`showGoogleCloudConfigDialog`)**: Displays the app's verified `Package Name` (`com.artha.money`), `Keystore SHA-1` (`B5:30:88:2A:98:73:E7:B2:01:68:B0:4F:89:21:4F:C0:25:14:A3:30`), and `SHA-256`, with 1-click clipboard copy buttons and a persistent `googleWebClientId` input.
  2. **Storage Access Framework (SAF) Direct Google Drive Integration:**
     - **Backup (`ActivityResultContracts.CreateDocument("application/json")`)**: Opens Android's system file picker with Google Drive natively in the sidebar drawer, streaming the encrypted JSON backup directly into the chosen Google Drive folder or device storage.
     - **Restore (`ActivityResultContracts.OpenDocument()`)**: Opens Android's file manager allowing the user to select backup files directly from Google Drive, restoring accounts, categories, rules, and all transactions atomically.








