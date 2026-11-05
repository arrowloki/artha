# Artha (अर्थ) — Native Android Offline-First Money & Net-Worth Tracker

<p align="center">
  <b>A calm, purposeful, encrypted, and offline-first personal financial ledger for Android.</b><br>
  <i>Zero bank linking · Zero telemetry · Zero cloud dependency · 100% On-Device & Encrypted</i>
</p>

---

## 📱 Download & Test on Mobile

Pre-built, pre-signed Android APK packages are ready to install on any Android 8.0+ (API 26+) phone:

- **[Download Signed Release APK (Recommended - 26 MB)](builds/artha-release-signed.apk)**
- **[Download Debug APK (35 MB)](builds/artha-debug.apk)**

*To install:* Download the APK onto your phone, tap to open, and allow *"Install from this source"* if prompted by Android.

---

## 🌟 Key Features

### 1. Zero Float/Double Minor Money Math
- Monetary amounts are strictly stored as 64-bit integers (`Long`) in minor currency units (e.g. ₹100.00 = `10000L`).
- Math operations enforce Banker's Rounding (`HALF_EVEN`) and Zero-Loss Split Distribution (guaranteeing $\sum \text{splits} = \text{total}$).
- Number formatting supports both Indian numbering (Lakhs & Crores: `₹1,50,000.00`) and International million/billion systems.

### 2. Single-Count Credit Card Rule
- An expense on a credit card raises the card liability and counts as an expense immediately on purchase date.
- Paying a credit card bill is recorded as a `TRANSFER` from an asset account to the liability account, ensuring payments **never** alter spending aggregates, expense categories, or budgets.

### 3. Natural Language 1-Sentence Entry
- Type or speak: e.g. `"45 coffee at blue tokai card"`, `"₹250 groceries at nature basket upi"`, `"salary 85000 bank"`.
- Live regex and token parsing extracts amount (supporting `k`, `L`/`lakh`, `cr`/`crore`), currency, transaction type, category, merchant, payment method, and relative dates (`yesterday`, `today`).

### 4. Interactive What-If Financial Scenario Simulator
- Projects Net Worth and monthly free cash flow trajectories over **1, 3, and 5 years** with compound investment growth ($r = (1 + R)^{1/12} - 1$).
- Simulates major decisions side-by-side against baseline status-quo:
  - Buying a ₹15L car (₹3L down payment + 48-month loan @ 9% p.a.).
  - Cutting recurring subscriptions and boosting high-yield SIP index investments.
  - 15% salary hikes with automatic savings diversion.
- Displays emergency liquid cash runway gauges and net worth crossover points.

### 5. Smart Rules Engine
- User-defined conditions (`MERCHANT`, `AMOUNT`, `NOTE`, `PAYMENT_METHOD`) triggering automated actions (`SET_CATEGORY`, `SET_PAYMENT_METHOD`, `EXCLUDE_FROM_BUDGET`).
- Automatically triggers on new entries and supports batch historical re-running across all past transactions with automatic category aggregate synchronization.

### 6. Loan EMI & Prepayment Simulation Engine
- Mathematical reducing-balance EMI calculation:
  $$\text{EMI} = \frac{P \cdot r \cdot (1 + r)^n}{(1 + r)^n - 1}$$
- Month-by-month amortization schedule with exact interest/principal split.
- Interactive Prepayment Simulator dynamically calculating interest saved and tenure shortened.

### 7. Planning & Safe-To-Spend Allowance
- Real-time Safe-To-Spend daily allowance calculation: remaining envelope divided by days left in cycle.
- Subscription annualization and renewal countdowns.
- AndroidX `WorkManager` daily periodic background job automatically committing due recurring rules.

### 8. Privacy, Biometrics & Security
- **SQLCipher 256-bit AES Encryption:** Database encrypted on SQLite in WAL mode with encryption key managed via `AndroidKeyStore`.
- **Biometric App Lock:** Fingerprint / Face authentication (`androidx.biometric.BiometricPrompt`) with Device PIN fallback.
- **Dynamic `FLAG_SECURE`:** Screen capture blocking and recents overview protection.
- **Bank CSV Auto-Detection:** Automatically maps arbitrary bank statement CSV columns (HDFC, ICICI, SBI, Chase).
- **Data Portability:** Full encrypted JSON backup and standard CSV export.

### 9. Home Screen Quick Glance Widget
- Native `AppWidgetProvider` displaying Today's Spending, Safe-To-Spend daily allowance, and a 1-tap `+ Quick Add` launcher.

---

## 🏗 Architecture & Tech Stack

- **Language:** Kotlin 2.x
- **UI:** Jetpack Compose, Material 3 with dynamic color tokens, single-activity edge-to-edge.
- **Architecture:** Multi-module Clean Architecture + MVVM:
  - `:app` — Application entry point, widgets, biometric locks.
  - `:core:model` — Pure Kotlin domain models and exact `Money` value class.
  - `:core:database` — Room SQLite in WAL mode with SQLCipher encryption, FTS5 virtual search, incremental aggregates.
  - `:core:domain` — Business logic: What-If simulation, EMI calculator, Safe-to-Spend engine, Sentence parser, Rules engine.
  - `:core:data` — Repositories, WorkManager tasks, Data portability, Scoped storage receipt manager.
  - `:core:ui` — Design tokens, Math keypad (`120+45*2`), theme presets.
  - `:feature:*` — `:transactions`, `:accounts`, `:planning`, `:insight` UI slices.
- **Build & Optimization:** Min SDK 26, Target SDK 34, R8 full mode with Proguard rules.

---

## 🛠 Building from Source

```bash
# Clone the repository
git clone https://github.com/arrowloki/artha.git
cd artha

# Run unit tests across all 10 modules
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build optimized release APK
./gradlew assembleRelease
```

---

## 📜 Documentation
Complete architectural decisions and design trade-offs are documented in [DECISIONS.md](DECISIONS.md).
