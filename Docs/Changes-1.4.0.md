# Release Changes - Version 1.4.0

This release introduces the local Notes feature, refines the app's About and Open Source licensing disclosures, modernizes copyright headers, and adds comprehensive database and developer documentation.

## 1. Local Notes Feature
Added an offline-first notes system allowing musicians to log, view, edit, and delete practice notes directly within the app.

*   **Database Schema & Migration (v2 → v3)**:
    *   Added `note` database entity (`Note.kt`) with auto-incrementing primary key `id`, `title` (max 100 chars), `content` (max 5000 chars), `profile_id` (indexed), `created_at`, and `modified_at`.
    *   Created `NoteDao.kt` extending `BaseDao<Note>`.
    *   Implemented database migration `PTDatabaseMigrationTwoToThree` in `PTDatabase.kt` and exported schema `3.json`.
    *   Registered static DAO accessor `PracticeTime.noteDao`.
*   **Notes UI & Navigation**:
    *   Created `NotesFragment.kt` and `NoteAdapter.kt` featuring a RecyclerView list, empty state hint, floating action button (FAB), and swipe/long-press delete dialogs.
    *   Added `notesFragment` destination to `nav_graph.xml` and wired overflow toolbar menu shortcuts across app fragments.
*   **Read-Only View Mode & Edit Toggle**:
    *   Opening an existing note launches in **Read-Only Mode** with an "Edit" neutral button on the dialog.
    *   Maintained 100% font contrast and styling in view mode by disabling input (`keyListener = null`, `isFocusable = false`, `isCursorVisible = false`) while keeping `isEnabled = true` to prevent Material graying/dimming.
*   **Entity Persistence & Update Fix**:
    *   Resolved a Kotlin data class limitation where calling `.copy()` on entities inheriting from `BaseModel` / `ModelWithTimestamps` dropped superclass fields (`id`, `createdAt`), resetting `id` to `0` and causing Room `@Update` queries to affect 0 rows.
    *   Refactored note saving to mutate existing entity fields directly (`existingNote.title = title`) before calling `dao.update()`.

## 2. Open Source Licenses & Dependency Attribution
Expanded the Open Source Licenses screen (`LicenseActivity.kt` & `activity_license.xml`) to list and credit all core open-source dependencies:

*   **MPAndroidChart** (by Philipp Jahoda - Apache 2.0)
*   **AppIntro** (by AppIntro Developers - Apache 2.0)
*   **Android Jetpack & AndroidX** (by Google / Android Open Source Project - Apache 2.0)
*   **Kotlin Coroutines** (by JetBrains - Apache 2.0)

## 3. "About the App" & Branding Content Updates
Updated legal and creator information on the "About the app" screen (`AboutAppActivity.kt` & `activity_about_app.xml`):

*   **Concept Credit**: Updated concept creator to *"Javier Carbone Sarmiento"*.
*   **Financing Disclosure**: Updated funding disclosure to *"The initial setup of this project was financed by the German music promotion program 'Neustart Kultur' by the GVL (Gesellschaft zur Verwertung von Leistungsschutzrechten mbH)"*.
*   **GitHub Repository Link**: Added a clickable repository URL (`https://github.com/PracticeTimeApp/PracticeTime`) under Design & Development that opens directly in the system web browser.

## 4. License & Copyright Header Modernization
*   **Copyright Headers**: Updated and standardized source code header comments across newly created and modified files to standard MIT license headers.
*   **`License.md`**: Updated license document header to copyright 2026 `PracticeTime! Authors`.

## 5. Documentation & Developer Guides
*   **Database Reference (`Docs/database.md`)**: Created comprehensive database documentation including entity schemas, ER diagram, DAO accessors, relation wrappers, and migration history.
*   **AI Agent Briefing (`AGENT.md`)**: Updated agent briefing guide with project structure details, safety guardrails (Feature Isolation, No Unrequested Git Commits), entity update guidelines, and Android 16 (API 36) build details.

---
**Summary:** Version 1.4.0 (`notes-feature` branch) delivers an offline Notes feature, full open-source license disclosures, updated About info, modernized copyright terms, and comprehensive database documentation.
