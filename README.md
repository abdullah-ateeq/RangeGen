# RangeGen

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material-3-757575?style=flat-square&logo=materialdesign&logoColor=white)](https://m3.material.io)

**RangeGen** is a modern, robust, and feature-rich Android application built with Kotlin and Jetpack Compose designed for generating precise numerical ranges, mathematical sequences, and step values. Whether you are working on data visualization, game development, math computations, or UI spacing grids, RangeGen provides lightning-fast calculations with high precision and full transparency.

---

## 📱 Screenshots & UI

<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" width="120" alt="RangeGen Logo">
</p>

---

## ✨ Key Features

### 1. Multiple Generation Modes
* **Points Mode:** Generate a fixed number of evenly spaced points between a start and end value (with options to include or exclude the endpoint).
* **Step Mode:** Generate a sequence advancing by a precise step increment from start to end.
* **Direct Value Lookup:** Instantly calculate and query specific values or indices within the generated range.

### 2. High Precision & Robust Validation
* Built using `BigDecimal` arithmetic to prevent floating-point rounding errors.
* Comprehensive validation engine (`RangeValidator`, `RangeGenParser`) providing clear warnings and error handling for edge cases (e.g., zero steps, inverted bounds, excessive point counts).

### 3. Formula & Step Transparency
* Inspect the underlying mathematical formulas and step-by-step calculation logic directly in the app.
* Understand *how* each value is computed with expandable breakdown views.

### 4. Calculation History & Persistence
* Automatically saves past range calculations.
* Browse history, copy results to clipboard, or re-load previous configurations with a single tap.

### 5. Deep Customization & Settings
* **Theming:** Full support for Light, Dark, and System default themes.
* **Dynamic Color:** Material 3 Dynamic Color support on supported Android devices.
* **Haptics & Animations:** Toggleable haptic feedback and smooth splash/transition animations.

---

## 🛠️ Tech Stack & Architecture

* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io) design system.
* **Architecture:** [MVVM (Model-View-ViewModel)](https://developer.android.com/topic/architecture) pattern adhering to Unidirectional Data Flow (UDF).
* **State Management:** Kotlin [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) & Coroutines.
* **Precision Math:** Java `BigDecimal` for arbitrary-precision decimal math.
* **Testing:** Comprehensive unit test suite covering calculators, parsers, formatters, validators, and repository logic (`JUnit 4`, `Truth`).

---

## 📂 Project Structure

```tree
RangeGen/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/com/example/rangegen/
│   │   │       ├── data/            # Repositories & Models (History, Settings)
│   │   │       ├── domain/math/     # Core calculation engine, validators & formatters
│   │   │       ├── ui/              # Jetpack Compose screens (Home, History, Settings, About, Splash)
│   │   │       └── util/            # Helpers (Haptics, Clipboard, etc.)
│   │   └── test/                    # Unit tests for domain math and calculation logic
│   └── build.gradle.kts
└── build.gradle.kts
```

---

## 🧪 Testing

RangeGen has a robust unit testing suite verifying all mathematical calculations, step generators, boundary conditions, and parsers.

To run the unit tests:
```bash
./gradlew testDebugUnitTest
```

---

## 🚀 Getting Started

### Prerequisites
* Android Studio (Ladybug, Narwhal, or newer recommended)
* JDK 17 or higher
* Android SDK (API level 24+)

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/RangeGen.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the `:app` module on an emulator or physical device.

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

Developed with ❤️ by [Abdullah Ateeq](https://github.com/abdullah-ateeq).
