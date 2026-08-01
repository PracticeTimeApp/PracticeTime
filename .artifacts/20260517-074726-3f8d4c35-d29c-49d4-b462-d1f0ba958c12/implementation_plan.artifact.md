# Implementation Plan - Session Time Modification Feature

Allow users to modify the date and time of existing practice sessions in the `FullscreenSessionActivity` (Edit Session screen).

## User Review Required

> [!IMPORTANT]
> The change will update the `createdAt` timestamp of the session and all its associated sections to maintain consistency. This will affect how the session appears in the history and statistics.

## Proposed Changes

### Database Layer

#### [SessionDao.kt](file:///D:/mis%20documentos/Development/PracticeTime/app/src/main/java/de/practicetime/practicetime/database/daos/SessionDao.kt)

- Update the `update` method to accept an optional `newTimestamp`.
- If `newTimestamp` is provided, update the `createdAt` field of the session and all its sections.
- Adjust goal progress calculation if necessary (though existing logic uses the section timestamps which will be updated).

### UI Layer

#### [activity_fullscreen_session.xml](file:///D:/mis%20documentos/Development/PracticeTime/app/src/main/res/layout/activity_fullscreen_session.xml)

- Add a new section for "Date & Time" similar to the Rating section.
- Display the current session start time.
- Add an edit icon/button to trigger date and time selection.

#### [FullscreenSessionActivity.kt](file:///D:/mis%20documentos/Development/PracticeTime/app/src/main/java/de/practicetime/practicetime/ui/sessionlist/FullscreenSessionActivity.kt)

- Add logic to show `DatePickerDialog` and `TimePickerDialog` when the user wants to edit the session time.
- Store the modified timestamp in a state variable.
- Pass the modified timestamp to `sessionDao.update`.

### Resources

#### [strings.xml](file:///D:/mis%20documentos/Development/PracticeTime/app/src/main/res/values/strings.xml)

- Ensure all necessary labels for the date/time editing are present.

## Verification Plan

### Automated Tests
- No existing unit tests for UI were found that cover this specific flow.
- I will verify the build with `gradle_build("app:assembleDebug")`.

### Manual Verification
1. Open the app and navigate to the "Sessions" tab.
2. Long-click a session and select "Edit" (or click if it opens directly).
3. Verify the new "Date & Time" section is visible.
4. Click the edit icon for date/time.
5. Change the date and time to a past value.
6. Click "Save".
7. Verify in the session list that the session now appears under the new date/time.
8. Verify in Statistics that the time is correctly accounted for on the new date.
