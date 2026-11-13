# AI Agent Guide - PracticeTime!

This document serves as a comprehensive briefing for AI agents working on the PracticeTime! project. It combines architectural knowledge, recent changes, and project-specific constraints.

## 1. Project Overview & Identity
PracticeTime! is an offline-first Android application for musicians to track their practice habits, set goals, and use practice tools (Metronome, Tuner, Recorder).

- **Privacy Principle**: The app DOES NOT collect PII and DOES NOT transmit data externally. Everything is stored in a local Room database.
- **Modernization**: Upgraded to target **Android 16 (API 36)**, using **Java 21** for Gradle builds with Java 8 / `desugar_jdk_libs` desugaring for compatibility down to Android 6.0 (API 23).
- **Feature Isolation Principle**: When working on a specific app feature, take maximum care and priority in not affecting negatively any other feature of the app.

## 2. Tech Stack
- **Language**: Kotlin 1.9.23
- **Build System**: Gradle 8.7 / AGP 8.4.0 (`namespace = "de.practicetime.practicetime"`, `buildConfig = true`). Current app version: **1.3.1** (`versionCode 19`).
- **UI**: Fragments (Jetpack Navigation `2.7.7`) + Activities (Material Components `1.11.0`).
- **Architecture**: MVVM (Model-View-ViewModel).
- **Persistence**: Room Database 2.6.1 (SQLite; exported schema in `app/schemas/`).
- **Asynchrony**: Kotlin Coroutines (`1.7.3`) & Flow.
- **Components**: Material Components, MPAndroidChart (`v3.1.0`), AppIntro (`6.2.0`).

## 3. Core Architecture & Project Structure
Single-module project (`:app`). Application class: `de.practicetime.practicetime.PracticeTime`.

- `database/`: Entities, DAOs, relation wrappers (`Relations.kt`), and `PTDatabase`. Detailed schema & ERD: `Docs/database.md`.
- `ui/`: UI components organized by feature package:
  - `activesession/`: Active session timer & metronome (`ActiveSessionActivity.kt`).
  - `goals/`: Practice goals management & history (`GoalsFragment.kt`, `GoalStatsActivity.kt`, `ArchivedGoalsActivity.kt`, `ProgressUpdateActivity.kt`).
  - `intro/`: First-run onboarding (`AppIntroActivity.kt`).
  - `library/`: Practice categories management (`LibraryFragment.kt`, `CategoryDialog.kt`).
  - `notes/`: Offline notes tracking (`NotesFragment.kt`, `NoteAdapter.kt`).
  - `overflowitems/`: Toolbar menu actions and info screens (`AboutActivity.kt`, `NotesOverflowItem.kt`, `TimerOverflowItem.kt`, etc.).
  - `sessionlist/`: Historical practice session log (`SessionListFragment.kt`, `SessionStatsActivity.kt`, `FullscreenSessionActivity.kt`).
  - `statistics/`: Overall practice stats overview (`StatisticsOverviewFragment.kt`).
  - `timer/`: Timer practice tool (`TimerFragment.kt`).
- `services/`: Foreground services for background timing and audio recording.
  - `SessionForegroundService`: Manages active practice sessions and metronome tick logic in the background.
  - `RecorderService`: Handles audio recording via microphone.
- `utils/`: Common helpers (formatting, timestamp helpers).
- `res/`: XML layouts, menus, navigation graph (`res/navigation/nav_graph.xml`), and resources.

## 4. Key Implementation Patterns
- **Manual Inset Handling (Android 15+)**: Do not use `fitsSystemWindows="true"` alone for root views. Instead, call `enableEdgeToEdge()` in `onCreate` and implement `ViewCompat.setOnApplyWindowInsetsListener` across the 7 core activities (`MainActivity`, `ActiveSessionActivity`, `GoalStatsActivity`, `SessionStatsActivity`, `ArchivedGoalsActivity`, `FullscreenSessionActivity`, `ProgressUpdateActivity`).
- **Dynamic Bottom Bar Heights**: For bottom button bars under Edge-to-Edge (e.g., `activity_active_session.xml` and `activity_fullscreen_session.xml`), use `wrap_content` with `minHeight="56dp"` to avoid cut-off buttons when system bar inset padding is applied.
- **Entity Mutation vs `.copy()`**: Room entities inherit `id` from `BaseModel` / `ModelWithTimestamps`. Kotlin's `data class .copy()` **only** copies constructor parameters and resets inherited `id` to `0`, breaking Room `@Update` statements (`WHERE id = 0`). **Always mutate existing entity properties directly** (`entity.title = title`) before calling `dao.update(entity)`. See `Docs/database.md`.
- **Dialog Read-Only & Edit States**: When presenting item details (e.g. notes in `NotesFragment`), open in Read-Only Mode first. To prevent dimmed/grayed-out text, keep `isEnabled = true` on `EditText` inputs and disable editing by setting `keyListener = null`, `isFocusable = false`, and `isCursorVisible = false`. Provide an "Edit" neutral button to switch to Edit Mode.
- **Media Resource Cleanup**: When playing custom audio via `MediaPlayer` (e.g., timer beep in `TimerFragment`), always attach `setOnCompletionListener { it.release() }` to release native audio resources immediately after playback.
- **Static Accessors**: `PracticeTime` (Application class) provides static access to DAOs (`sessionDao`, `sectionDao`, `categoryDao`, `goalDescriptionDao`, `goalInstanceDao`, `noteDao`) and common utilities.
- **Broadcasting & Services**: `LocalBroadcastManager` is used for internal service communication between `RecorderService` and `ActiveSessionActivity`.

## 5. Recent Version Changes (1.2.0, 1.3.0 & 1.3.1)
- **Infrastructure (v1.2.0)**: Upgraded Gradle Wrapper (`7.2` -> `8.7`), AGP (`7.1.1` -> `8.4.0`), Kotlin (`1.6.10` -> `1.9.23`), and Room (`2.4.1` -> `2.6.1` to fix DAO `suspend` compilation). Moved package name to `namespace` property and enabled `buildConfig = true`.
- **Edge-to-Edge (v1.2.0)**: Implemented Android 15 edge-to-edge support across 7 core activities (`MainActivity`, `ActiveSessionActivity`, `GoalStatsActivity`, `SessionStatsActivity`, `ArchivedGoalsActivity`, `FullscreenSessionActivity`, `ProgressUpdateActivity`). Adjusted bottom button bar layouts to `wrap_content` + `minHeight="56dp"`.
- **Category Colors (v1.2.0)**: Expanded category colors from 10 to 20 (`category_color_11` to `20`), added 2 new `RadioGroup` rows to `dialog_add_or_change_category.xml`, and updated `CategoryDialog.kt` logic for mutually exclusive selection across all 20 options.
- **Tuner (v1.2.0 / Feature Branch)**: Added a chromatic tuner fragment using autocorrelation.
- **Git Maintenance (v1.2.0)**: Added `/release` and `/keystore` to `.gitignore`.
- **Custom Beep Audio (v1.3.0)**: Replaced system `ToneGenerator` with `MediaPlayer` playing `res/raw/custom_beep.mp3` in `TimerFragment.kt`, ensuring resource cleanup via `setOnCompletionListener { it.release() }`.
- **Target SDK (v1.2.0, v1.3.0 & v1.3.1)**: Updated `compileSdk` and `targetSdk` from `34` -> `35` in v1.2.0, and from `35` -> `36` in v1.3.0/v1.3.1 to comply with Google Play Console requirements.
- **Notes Feature (v1.3.1)**: Integrated local notes management (`Note` entity, `NoteDao`, database migration v2 -> v3). Fixed database update persistence by direct entity mutation (preventing Kotlin `data class .copy()` primary key loss). Implemented read-only view mode upon opening notes with an "Edit" button to toggle edit mode, ensuring font styling and contrast remain identical across view and edit states.

## 6. Development Rules for Agents
1. **Feature Isolation & Non-Regression**: When working on a specific app feature, take maximum care and priority in not affecting negatively any other feature of the app.
2. **Branch Hygiene**: 
   - Feature work goes into dedicated branches (e.g., `tuner`, `countdown-beep-1.3.0`).
   - `master` should only receive stable, approved merges.
3. **Git & Secret Security**: Never commit binary build outputs (`/release`) or signing credentials (`/keystore`), which must remain in `.gitignore`.
4. **UI Integrity**: Always check that bottom navigation and top toolbars respect system insets, and use `wrap_content` with `minHeight="56dp"` for dynamic bottom bars.
5. **Strings**: All user-facing text must be defined in `strings.xml`.
6. **Build Status**: Ensure `clean assembleDebug` passes after every major change.
7. **Skill Utilization**: Use `.github/skills/dependency_audit.md` for checking library versions.
8. **No Unrequested Git Commits**: Do NOT execute `git commit`. All git commits are handled directly by the user.

## 7. Known Constraints
- **Simulation**: Testing audio (Tuner/Beep) on simulators can be inconsistent. Always prefer real device verification for audio features.
- **Transitive Resources**: `android.nonTransitiveRClass=false` is set in `gradle.properties` to ensure resource resolution consistency in this legacy codebase.

---
*Refer to `Docs/Changes-1.2.0.md`, `Docs/Changes-1.3.0.md`, and `Docs/database.md` for chronological history and database architecture.*
