# AI Agent Guide - PracticeTime!

This document serves as a comprehensive briefing for AI agents working on the PracticeTime! project. It combines architectural knowledge, recent changes, and project-specific constraints.

## 1. Project Overview & Identity
PracticeTime! is an offline-first Android application for musicians to track their practice habits, set goals, and use practice tools (Metronome, Tuner, Recorder).

- **Privacy Principle**: The app DOES NOT collect PII and DOES NOT transmit data externally. Everything is stored in a local Room database.
- **Modernization**: Upgraded to support **Android (API 36)** and **Java 21**.

## 2. Tech Stack
- **Language**: Kotlin 1.9.23
- **Build System**: Gradle 8.7 / AGP 8.4.0 (`namespace` configured in `build.gradle`, `buildConfig = true` enabled).
- **UI**: Fragments (Jetpack Navigation) + Activities.
- **Architecture**: MVVM (Model-View-ViewModel).
- **Persistence**: Room Database 2.6.1 (SQLite; resolves DAO `suspend` function compilation).
- **Asynchrony**: Kotlin Coroutines & Flow.
- **Components**: Material Components, MPAndroidChart, AppIntro.

## 3. Core Architecture & Project Structure
Single-module project (`:app`).

- `database/`: Entities, DAOs, and `PTDatabase`.
- `ui/`: Features organized by package (`activesession`, `goals`, `library`, `statistics`, `tuner`).
- `services/`: Foreground services for background timing and audio recording.
  - `SessionForegroundService`: Manages active practice sessions and metronome.
  - `RecorderService`: Handles audio recording.
- `utils/`: Common helpers (formatting, etc.).
- `res/`: XML layouts, menus, and resources.

## 4. Key Implementation Patterns
- **Manual Inset Handling (Android 15)**: Do not use `fitsSystemWindows="true"` alone for root views. Instead, call `enableEdgeToEdge()` in `onCreate` and implement `ViewCompat.setOnApplyWindowInsetsListener` across core activities (`MainActivity`, `ActiveSessionActivity`, `GoalStatsActivity`, `SessionStatsActivity`, `ArchivedGoalsActivity`, `FullscreenSessionActivity`, `ProgressUpdateActivity`).
- **Dynamic Bottom Bar Heights**: For bottom button bars under Edge-to-Edge (e.g., `activity_active_session.xml` and `activity_fullscreen_session.xml`), use `wrap_content` with `minHeight="56dp"` to avoid cut-off buttons when system bar inset padding is applied.
- **Media Resource Cleanup**: When playing custom audio via `MediaPlayer` (e.g., timer beep in `TimerFragment`), always attach `setOnCompletionListener { it.release() }` to release native audio resources immediately after playback.
- **Static Accessors**: `PracticeTime` (Application class) provides static access to DAOs and common utilities.
- **Broadcasting**: `LocalBroadcastManager` is used for internal communication (e.g., recording duration updates).

## 5. Recent Version Changes (1.2.0 & 1.3.0)
- **Infrastructure (v1.2.0)**: Upgraded Gradle Wrapper (`7.2` -> `8.7`), AGP (`7.1.1` -> `8.4.0`), Kotlin (`1.6.10` -> `1.9.23`), and Room (`2.4.1` -> `2.6.1` to fix DAO `suspend` compilation). Moved package name to `namespace` property and enabled `buildConfig = true`.
- **Edge-to-Edge (v1.2.0)**: Implemented Android 15 edge-to-edge support across 7 core activities (`MainActivity`, `ActiveSessionActivity`, `GoalStatsActivity`, `SessionStatsActivity`, `ArchivedGoalsActivity`, `FullscreenSessionActivity`, `ProgressUpdateActivity`). Adjusted bottom button bar layouts to `wrap_content` + `minHeight="56dp"`.
- **Category Colors (v1.2.0)**: Expanded category colors from 10 to 20 (`category_color_11` to `20`), added 2 new `RadioGroup` rows to `dialog_add_or_change_category.xml`, and updated `CategoryDialog.kt` logic for mutually exclusive selection across all 20 options.
- **Tuner (v1.2.0 / Feature Branch)**: Added a chromatic tuner fragment using autocorrelation.
- **Git Maintenance (v1.2.0)**: Added `/release` and `/keystore` to `.gitignore`.
- **Custom Beep Audio (v1.3.0)**: Replaced system `ToneGenerator` with `MediaPlayer` playing `res/raw/custom_beep.mp3` in `TimerFragment.kt`, ensuring resource cleanup via `setOnCompletionListener { it.release() }`.
- **Target SDK (v1.2.0 & v1.3.0)**: Updated `compileSdk` and `targetSdk` from `34` -> `35` in v1.2.0, and from `35` -> `36` in v1.3.0 to comply with Google Play Console requirements.

## 6. Development Rules for Agents
1. **Branch Hygiene**: 
   - Feature work goes into dedicated branches (e.g., `tuner`, `countdown-beep-1.3.0`).
   - `master` should only receive stable, approved merges.
2. **Git & Secret Security**: Never commit binary build outputs (`/release`) or signing credentials (`/keystore`), which must remain in `.gitignore`.
3. **UI Integrity**: Always check that bottom navigation and top toolbars respect system insets, and use `wrap_content` with `minHeight="56dp"` for dynamic bottom bars.
4. **Strings**: All user-facing text must be defined in `strings.xml`.
5. **Build Status**: Ensure `clean assembleDebug` passes after every major change.
6. **Skill Utilization**: Use `.github/skills/dependency_audit.md` for checking library versions.

## 7. Known Constraints
- **Simulation**: Testing audio (Tuner/Beep) on simulators can be inconsistent. Always prefer real device verification for audio features.
- **Transitive Resources**: `android.nonTransitiveRClass=false` is set in `gradle.properties` to ensure resource resolution consistency in this legacy codebase.

---
*Refer to `Changes-1.2.0.md` and `Changes-1.3.0.md` for a chronological history of recent updates.*
