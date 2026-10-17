# AI Agent Guide - PracticeTime!

This document serves as a comprehensive briefing for AI agents working on the PracticeTime! project. It combines architectural knowledge, recent changes, and project-specific constraints.

## 1. Project Overview & Identity
PracticeTime! is an offline-first Android application for musicians to track their practice habits, set goals, and use practice tools (Metronome, Tuner, Recorder).

- **Privacy Principle**: The app DOES NOT collect PII and DOES NOT transmit data externally. Everything is stored in a local Room database.
- **Modernization**: Recently upgraded to support **Android 15 (API 35)** and **Java 21**.

## 2. Tech Stack
- **Language**: Kotlin 1.9.23
- **Build System**: Gradle 8.7 / AGP 8.4.0
- **UI**: Fragments (Jetpack Navigation) + Activities.
- **Architecture**: MVVM (Model-View-ViewModel).
- **Persistence**: Room Database (SQLite).
- **Asynchrony**: Kotlin Coroutines & Flow.
- **Components**: Material Components, MPAndroidChart, AppIntro.

## 3. Core Architecture & Project Structure
Single-module project (`:app`).

- `database/`: Entities, DAOs, and `PTDatabase`.
- `ui/`: Features organized by package (activesession, goals, library, statistics, tuner).
- `services/`: Foreground services for background timing and audio recording.
  - `SessionForegroundService`: Manages active practice sessions and metronome.
  - `RecorderService`: Handles audio recording.
- `utils/`: Common helpers (formatting, etc.).
- `res/`: XML layouts, menus, and resources.

## 4. Key Implementation Patterns
- **Manual Inset Handling (Android 15)**: Do not use `fitsSystemWindows="true"` alone for root views. Instead, use `enableEdgeToEdge()` in `onCreate` and implement `ViewCompat.setOnApplyWindowInsetsListener` to manually apply system bar padding.
- **Static Accessors**: `PracticeTime` (Application class) provides static access to DAOs and common utilities.
- **Broadcasting**: `LocalBroadcastManager` is used for internal communication (e.g., recording duration updates).

## 5. Recent Version Changes (1.2.0 & 1.3.0)
- **Infrastructure (v1.2.0)**: Upgraded AGP (8.4.0), Kotlin (1.9.23), and Room (2.6.1).
- **Edge-to-Edge (v1.2.0)**: Full implementation of Android 15 edge-to-edge support across all activities.
- **Colors (v1.2.0)**: Expanded category colors from 10 to 20.
- **Tuner (v1.2.0)**: Added a chromatic tuner fragment using autocorrelation.
- **Loop-Timer & Custom Beep (v1.2.0 - v1.3.0)**: Added loopable timer and custom MP3 beep notifications (`res/raw/custom_beep.mp3` played via `MediaPlayer` in `TimerFragment`).

## 6. Development Rules for Agents
1. **Branch Hygiene**: 
   - Feature work goes into dedicated branches (e.g., `tuner`, `countdown-beep-1.3.0`).
   - `master` should only receive stable, approved merges.
2. **UI Integrity**: Always check that bottom navigation and top toolbars respect system insets.
3. **Strings**: All user-facing text must be defined in `strings.xml`.
4. **Build Status**: Ensure `clean assembleDebug` passes after every major change.
5. **Skill Utilization**: Use the `.github/skills/dependency_audit.md` for checking library versions.

## 7. Known Constraints
- **Simulation**: Testing audio (Tuner/Beep) on simulators can be inconsistent. Always prefer real device verification for audio features.
- **Transitive Resources**: `android.nonTransitiveRClass=false` is set in `gradle.properties` to ensure resource resolution consistency in this legacy codebase.

---
*Refer to `Changes-1.2.0.md` and `Changes-1.3.0.md` for a chronological history of recent updates.*
