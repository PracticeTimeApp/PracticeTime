# Release Changes - Version 1.2.0

This release focuses on modernization for Android 15 compatibility, build infrastructure upgrades, and expanding user customization options.

## 1. Build Infrastructure & Dependency Upgrades
Fixed initial build errors and ensured compatibility with modern Android environments.

*   **Gradle Wrapper Upgrade (`gradle-wrapper.properties`)**: Upgraded from `7.2` to `8.7`.
    *   *Reason:* Required to support Java 21 and newer Android Gradle Plugin versions.
*   **Android Gradle Plugin Upgrade (`build.gradle`)**: Upgraded from `7.1.1` to `8.4.0`.
    *   *Reason:* Required for compatibility with Android 14/15 and modern build tools.
*   **Kotlin Upgrade (`build.gradle`)**: Upgraded from `1.6.10` to `1.9.23`.
    *   *Reason:* Needed for compatibility with the newer Gradle plugin and modern language features.
*   **Room Database Upgrade (`app/build.gradle`)**: Upgraded from `2.4.1` to `2.6.1`.
    *   *Reason:* Resolved critical compilation errors where `suspend` functions in DAOs were incorrectly handled by the older Room compiler.
*   **Target SDK Update**: Increased `compileSdk` and `targetSdk` from `34` to `35`.
    *   *Reason:* Compliance with Google Play Store requirements for Android 15.
*   **Namespace & BuildConfig Configuration**: Moved package name to `namespace` property and explicitly enabled `buildConfig = true`.
    *   *Reason:* Standardized configuration for AGP 8.0+ requirements.

## 2. Category Colors Feature
Expanded the color palette to provide more customization for practice categories.

*   **New Color Definitions**: Added 10 new vibrant colors (`category_color_11` to `20`) in `colors.xml`.
*   **Resource Integration**: Included the 10 new colors in the global `category_colors` array in `category_colors.xml`.
*   **UI Enhancements**:
    *   Added two new rows (`RadioGroup`) to `dialog_add_or_change_category.xml` to display the new options.
    *   Updated `CategoryDialog.kt` logic to manage the expanded set of 20 color buttons, ensuring mutually exclusive selection across all rows.

## 3. Android 15 Edge-to-Edge & UI Fixes
Resolved layout collisions where app content overlapped with system UI elements (status bar and navigation bar).

*   **Edge-to-Edge Activation**: Integrated `enableEdgeToEdge()` in the `onCreate` method of all major Activities.
*   **Manual Inset Handling**: Implemented `ViewCompat.setOnApplyWindowInsetsListener` across core Activities (`MainActivity`, `ActiveSessionActivity`, `GoalStatsActivity`, `SessionStatsActivity`, `ArchivedGoalsActivity`, `FullscreenSessionActivity`, `ProgressUpdateActivity`).
    *   *Reason:* Programmatically calculates system bar heights to apply precise padding, preventing overlap with the status bar and navigation handle.
*   **Layout Adjustments**:
    *   Modified `activity_active_session.xml` and `activity_fullscreen_session.xml` bottom button bars.
    *   *Change:* Switched height from fixed `56dp` to `wrap_content` with a `minHeight` of `56dp`.
    *   *Reason:* Prevents buttons from being cut off or "squashed" when navigation handle padding is applied.
    *   *Update:* Dynamically adjusted margins for main content and pause overlays in `ActiveSessionActivity` to perfectly align with the new dynamic bar height.

## 4. Git Maintenance
*   **`.gitignore` Improvements**: Added `/release` and `/keystore` to the exclusion lists.
    *   *Reason:* Prevents accidental commits of binary build artifacts and sensitive signing credentials.

---
**Summary:** The `add-more-category-colors` branch provides a modernized, Android 15-ready version of the app with an improved build pipeline and expanded category customization.
