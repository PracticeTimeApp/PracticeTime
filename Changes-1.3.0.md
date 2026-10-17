# Release Changes - Version 1.3.0

This release introduces improvements and customization options for the Practice timer feature, specifically enhancing the completion beep notification system.

## 1. Custom Beep Audio Support
Replaced the legacy system tone generator with a robust custom audio playback mechanism for timer completion notifications.

*   **Custom Audio Resource (`res/raw/custom_beep.mp3`)**: Added a custom audio beep file to raw resources.
    *   *Note:* Filename uses underscores (`custom_beep.mp3`) to comply with Android AAPT resource naming conventions.
*   **`MediaPlayer` Integration (`TimerFragment.kt`)**: 
    *   Replaced the restricted system `ToneGenerator` (which clamped or ignored custom durations) with `MediaPlayer` playing `R.raw.custom_beep`.
    *   Ensured proper resource release (`setOnCompletionListener { it.release() }`) upon playback finish.
    *   Allows fully customizable sound duration, quality, and audio profile matching user preferences.

## 2. Target SDK Update (API 36)
*   Increased `compileSdk` and `targetSdk` from `35` to `36` in `app/build.gradle` to comply with Google Play Console target API requirements.

---
**Summary:** Version 1.3.0 (`countdown-beep-1.3.0` branch) delivers flexible timer beep notifications powered by custom audio resources and targets API 36.
