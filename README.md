# iBAS Sahayak (iBAS সহায়ক)

**iBAS Sahayak** is an offline-first Android companion app built with **Kotlin & Jetpack Compose** for **Bangladesh Government Service Holders**. It provides manual, rule-accurate equivalents of key **iBAS++** calculations and official office documentation workflows in both **বাংলা (Bengali)** and **English**.

---

## Features

### 1. Manual Pay Bill Copy (বেতন বিল কপি)
- Built-in **National Pay Scale 2015** (Grades 1–20 with official increment steps).
- Automatic **House Rent Allowance (HRA)** calculation based on posting tier (*Dhaka Metro*, *Divisional / Major Cities*, *Other Districts & Upazilas*).
- Medical, Education (up to 2 children), Tiffin, Washing, Charge, and **Special Benefit (5%)** allowances.
- Deductions for **GPF Subscription (%)**, **GPF Loan Installment**, **Benevolent Fund**, **Group Insurance**, **Govt. Quarter Rent (7.5%)**, and **Income Tax (TDS)**.
- Supports **Monthly Salary**, **Festival Bonus (Eid/Puja)**, **Boishakhi Allowance (20%)**, and **RRL Allowance** bills.
- **Amount in Words**, one-tap **Copy**, **Share PDF**, **Print / Save PDF**, and local **Bill History**.

### 2. GPF Calculator (জিপিএফ ক্যালকুলেটর)
- Fiscal Year (**July – June**) progressive monthly ledger.
- Supports Bangladesh Finance Division slab interest rates (**13% / 12% / 11%**) or custom flat rates.
- Accounts for opening balance, monthly subscription, refundable loan recovery, and mid-year withdrawals.
- Built-in **GPF Advance Eligibility (80%)** & **Installment (EMI) Calculator**.

### 3. Pension, Gratuity & PRL Calculator (পেনশন ও গ্র্যাচুইটি)
- Official qualifying service pension percentage table (**21% at 5 years up to 90% at 25+ years**).
- Mandatory **50% surrender/commutation** and official **Gratuity rate** (`৳ 230 / 245 / 260 / 265` per Taka).
- Monthly net pension, age-tiered **Medical Allowance** (`৳ 1,500` / `৳ 2,500`), Festival Bonus, and Boishakhi Allowance.
- Automatic **PRL Start Date** and **Final Retirement Date** calculation from Date of Birth (supports 59 general / 60 freedom fighter age rules).

### 4. Arrear Salary Calculator (বকেয়া বেতন)
- Date-range **Due vs Drawn** salary comparison with partial-month day proration.
- Includes House Rent Allowance differences, Festival Bonus arrears, and GPF deduction adjustments.

### 5. Official Leave & Joining Applications (আবেদনপত্র জেনারেটর)
Ready-to-print official Bengali & English application letters with live A4 preview, PDF export, and local draft saving:
- **RRL** — Rest & Recreation Leave (শ্রান্তি ও বিনোদন ছুটি ও ভাতা)
- **CL** — Casual Leave (নৈমিত্তিক ছুটি)
- **EL** — Earned Leave (অর্জিত ছুটি)
- **ML** — Medical Leave (চিকিৎসা ছুটি)
- **Joining Report** — Resuming duty after leave (ছুটি শেষে কর্মস্থলে যোগদানপত্র)
- **GPF Advance** & **PRL** Applications

---

## Tech Stack & Architecture

- **Language:** Kotlin 2.2
- **UI:** Jetpack Compose + Material Design 3 (Adaptive `NavigationBar` / `NavigationRail`, Edge-to-Edge)
- **Architecture:** MVVM (`AndroidViewModel` + `StateFlow` + Repository pattern)
- **Local Persistence:**
  - **Room Database** (`SavedPayBillEntity`, `SavedApplicationEntity`)
  - **DataStore Preferences** (`EmployeeProfile` & language preference)
- **PDF & Printing:** Android `PdfDocument`, `PrintManager`, and `FileProvider` (zero broad storage permissions required)
- **Bundled Typography:** `Hind Siliguri`, `Plus Jakarta Sans`, `JetBrains Mono`

---

## Project Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                  # Navigation Compose host & adaptive shell
├── data/
│   ├── AppDatabase.kt               # Room database, entities, DAO, repository
│   └── ProfilePreferences.kt        # DataStore repository for employee profile
├── domain/
│   ├── PayScale2015.kt              # Grades 1-20, HRA slabs, PayBillCalculator
│   ├── GpfCalculatorEngine.kt       # Fiscal year GPF ledger & slab interest engine
│   ├── PensionCalculatorEngine.kt   # Pension, gratuity & PRL date calculator
│   ├── ArrearCalculatorEngine.kt    # Date-range Due vs Drawn arrear engine
│   └── ApplicationTemplates.kt      # Official Bengali/English letter generator
├── ui/
│   ├── IbasViewModel.kt             # Shared state & actions
│   ├── components/CommonComponents.kt
│   ├── screens/                     # Home, PayBill, GPF, Pension, Arrear, Applications, PayScale, Profile
│   └── theme/                       # Civic Emerald M3 theme & typography
└── util/
    └── PdfExporter.kt               # A4 PDF generation, PrintManager & Share sheet
```

---

## Getting Started (Build & Run)

### Prerequisites
- Android Studio Ladybug / Meerkat or newer (JDK 17+)
- Android SDK 36 (`minSdk 24`, `targetSdk 36`)
- Gradle 9.3.1 (if building from CLI without IDE)

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/ibas-sahayak.git
cd ibas-sahayak
```

### 2. Restore debug keystore (required for `debugConfig` signing)
```bash
base64 -d debug.keystore.base64 > debug.keystore
```

### 3. Build & Test
Using Android Studio, open the project root and click **Run**, or build via Gradle CLI:
```bash
gradle testDebugUnitTest
gradle assembleDebug
```

*(For release builds, configure `KEYSTORE_PATH`, `STORE_PASSWORD`, and `KEY_PASSWORD` environment variables.)*

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
