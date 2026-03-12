# Contributing

Thanks for taking the time to contribute. This document covers the basics — how to report bugs, suggest features, and submit pull requests.

## Before you start

- Check the [open issues](https://github.com/Ryggs/Calculator/issues) to see if someone's already working on what you have in mind.
- For anything beyond a small fix, open an issue first so we can talk through the approach before you spend time on it.

## Development setup

1. Fork the repo and clone your fork:
   ```bash
   git clone https://github.com/<your-username>/Calculator.git
   cd Calculator
   ```

2. Open the project in **Android Studio Hedgehog** or newer with JDK 17.

3. Run the existing tests before making any changes, just to make sure your setup is clean:
   ```bash
   ./gradlew testDebugUnitTest
   ```

4. Create a branch off `master` named after what you're doing:
   ```bash
   git checkout -b fix/expression-parser-edge-case
   # or
   git checkout -b feature/history-log
   ```

## Project structure

| Module | What lives there |
|---|---|
| `shared/` | Expression parser, calculator logic — shared between Android and iOS |
| `androidApp/` | Android UI built with Jetpack Compose |
| `iosApp/` | iOS UI built with SwiftUI |

If you're fixing a math or parsing bug, the change almost certainly belongs in `shared/`. UI-only changes go in the respective platform module.

## Making changes

- Keep changes focused. One bug fix or one feature per PR — mixing things makes review harder.
- If you're touching the expression parser, add or update tests in `shared/src/commonTest/`.
- Run the test suite before pushing:
  ```bash
  ./gradlew testDebugUnitTest
  ```
- Make sure the Android app still builds:
  ```bash
  ./gradlew :androidApp:assembleDebug
  ```

## Commit style

No strict format required, but keep commit messages short and descriptive:

```
fix: handle empty expression before evaluation
feat: add history of last 5 calculations
chore: update Compose BOM to 2024.06.00
```

## Submitting a pull request

1. Push your branch to your fork.
2. Open a PR against `master` on this repo.
3. Fill out the PR description — what changed and why.
4. If your PR fixes an open issue, link it (`Closes #42`).

PRs are reviewed as time allows. If you don't hear back within a week, feel free to ping the thread.

## Reporting bugs

Use the [bug report template](.github/ISSUE_TEMPLATE/bug_report.md). The more detail you include — device, Android version, what you tapped, what you expected — the faster it gets fixed.

## Suggesting features

Open a [feature request](.github/ISSUE_TEMPLATE/feature_request.md) and describe what you want and why. Screenshots or mockups are always helpful.

## Code of conduct

Be decent to each other. That's it.
