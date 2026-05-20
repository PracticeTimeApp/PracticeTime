# Architecture Overview

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.6.10 (100%) |
| Platform | Android (AndroidX) |
| Build System | Gradle 7.1.1 / AGP 7.1.1 |
| UI | Fragments + ViewBinding |
| Architecture Pattern | MVVM (Model–View–ViewModel) |
| Local Database | Room (SQLite) |
| Async | Kotlin Coroutines |
| DI | Manual / Application-scoped singletons |
| Navigation | Jetpack Navigation Component |

---

## Project Structure

The repository is a **single-module Android project**. All application code lives under the `:app` Gradle module.

```
PracticeTime/
├── app/
│   └── src/
│       └── main/
│           ├── java/de/practicetime/practicetime/
│           │   ├── database/          # Room entities, DAOs, and database class
│           │   ├── ui/                # Fragments and Activities
│           │   │   ├── activesession/ # Active practice session screen
│           │   │   ├── goals/         # Goals management screen
│           │   │   ├── library/       # Practice library screen
│           │   │   ├── statistics/    # Statistics screen
│           │   │   ├── metronome/     # Metronome screen
│           │   │   └── recorder/      # Audio recorder screen
│           │   ├── viewmodels/        # ViewModels per feature
│           │   └── utils/             # Shared helpers and extensions
│           ├── res/                   # Layouts, drawables, strings, menus
│           └── AndroidManifest.xml
├── build.gradle                       # Root build config (AGP + Kotlin plugins)
├── settings.gradle                    # Single-module include (':app')
└── gradle.properties                  # AndroidX, Jetifier, JVM args
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                        UI Layer                         │
│  MainActivity  ──► BottomNavigation                     │
│       │                                                  │
│       ├── ActiveSessionFragment  (time tracker)         │
│       ├── LibraryFragment        (practice items)       │
│       ├── GoalsFragment          (daily/weekly goals)   │
│       ├── StatisticsFragment     (charts & progress)    │
│       ├── MetronomeFragment      (in-app metronome)     │
│       └── RecorderFragment       (audio capture)        │
└──────────────────┬──────────────────────────────────────┘
                   │  observes (LiveData / StateFlow)
┌──────────────────▼──────────────────────────────────────┐
│                    ViewModel Layer                       │
│  ActiveSessionViewModel   GoalsViewModel                │
│  LibraryViewModel         StatisticsViewModel           │
│       (Kotlin Coroutines for background work)           │
└──────────────────┬──────────────────────────────────────┘
                   │  suspend functions / Flow
┌──────────────────▼──────────────────────────────────────┐
│                     Data Layer                          │
│                                                         │
│  ┌──────────────────────────────────────────────┐       │
│  │              Room Database                   │       │
│  │  (PTDatabase)                                │       │
│  │                                              │       │
│  │  Entities          DAOs                      │       │
│  │  ─────────         ─────────────────         │       │
│  │  PracticeSection   PracticeSectionDao        │       │
│  │  PracticeSession   PracticeSessionDao        │       │
│  │  Category          CategoryDao               │       │
│  │  Goal              GoalDao                   │       │
│  └──────────────────────────────────────────────┘       │
│                                                         │
│  Local SQLite (on-device, no network layer)             │
└─────────────────────────────────────────────────────────┘
```

---

## Key Architectural Decisions

**Single-module layout.** All feature code is co-located under one Gradle module. This keeps the build simple and the codebase easy to navigate for a project of this scale.

**MVVM with Room + Coroutines.** ViewModels expose UI state as `LiveData` or `StateFlow`. DAOs return `Flow<List<T>>` so the UI automatically reacts to database changes without manual polling.

**Fragment-based navigation.** A single `MainActivity` hosts a `BottomNavigationView` and a `NavHostFragment`. The Jetpack Navigation Component manages the back stack and fragment transactions between the six main screens.

**No network layer.** All data is stored locally on-device using Room. There is no remote API, no authentication, and no sync service, which keeps the app fully offline and private by design.

**Feature-per-package organisation.** Each major feature (active session, library, goals, statistics, metronome, recorder) is grouped in its own sub-package under `ui/`, keeping fragments, adapters, and supporting classes for each feature close together.

---

## Data Model Summary

| Entity | Purpose |
|---|---|
| `Category` | A labelled practice item (e.g. "Bach Partita", "Scales") |
| `PracticeSection` | A timed block within a session, linked to a `Category` |
| `PracticeSession` | A completed practice session (date, duration, rating, comment) |
| `Goal` | A time target for a `Category` over a daily/weekly/monthly period |

Sessions are built from one or more `PracticeSections`, each associated with a `Category`. This lets statistics be broken down by item, not just total time.

---

## Build Configuration

```
Android Gradle Plugin : 7.1.1
Kotlin                : 1.6.10
Min SDK               : (defined in app/build.gradle)
Target/Compile SDK    : (defined in app/build.gradle)
Repositories          : Google, MavenCentral, JitPack, JCenter
AndroidX              : enabled
Jetifier              : enabled
```

