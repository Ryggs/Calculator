# Calculator

A clean, neumorphic calculator for Android and iOS, built with Kotlin Multiplatform. The shared logic lives in one place — the UI is native on each platform.

<p align="left">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/iOS-000000?style=flat&logo=apple&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin_Multiplatform-7F52FF?style=flat&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-4285F4?style=flat&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/license-MIT-blue?style=flat" />
</p>

---

## What it does

Nothing fancy — it's a calculator. But it does the job well:

- Addition, subtraction, multiplication, division
- Square root and percentage
- Chained expressions with correct operator precedence
- `BigDecimal` math, so `0.1 + 0.2` doesn't embarrass you
- Light / dark theme toggle
- Neumorphic button design with a glass-effect LCD display

## Screenshots

> Coming soon — drop some in a PR if you have them!

## Getting started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- Xcode 15+ (iOS only)
- An Android device or emulator running API 24+

### Clone & run

```bash
git clone https://github.com/Ryggs/Calculator.git
cd Calculator
```

**Android**

Open the project in Android Studio and run the `androidApp` configuration, or:

```bash
./gradlew :androidApp:installDebug
```

**iOS**

Open `iosApp/iosApp.xcodeproj` in Xcode, select your simulator or device, then hit Run.

## Project layout

```
Calculator/
├── androidApp/          # Android UI (Jetpack Compose)
├── iosApp/              # iOS UI (SwiftUI)
└── shared/              # KMP module — calculator logic shared across platforms
    └── commonMain/
        └── Calculator.kt
```

The `shared` module is where the expression parser and evaluator live. Both apps call into it directly, so any fix or improvement there benefits both platforms at once.

## How the math works

Expressions are parsed with a hand-rolled recursive descent parser. It handles:

- Standard operator precedence (`*` and `/` before `+` and `-`)
- Parentheses
- Negative numbers
- Decimal inputs

Division by zero throws an `ArithmeticException` and surfaces as `Error` in the UI. All arithmetic is done with `BigDecimal` to avoid floating-point drift.

## Contributing

PRs are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to get started, what to work on, and how to submit changes.

## License

MIT — see [LICENSE](LICENSE) for the full text.

## Privacy

No data leaves your device. No analytics, no ads, no permissions beyond what's needed to run. See [PRIVACY_POLICY.md](PRIVACY_POLICY.md) for details.
