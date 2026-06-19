# Skill: Dependency Version Audit

## Objective
Provide a clear overview of the project's dependencies, comparing current versions against the latest available stable versions.

## Constraints
1. **Branch Check**: Before performing any audit, the agent MUST verify that the current branch is `master`. If not on `master`, the agent should notify the user and ask to switch before proceeding.
2. **Read-Only**: This skill is for information gathering only. DO NOT modify any files or upgrade dependencies automatically.

## Instructions
1. **Verify Branch**: Run `git branch` and ensure `* master` is the active branch.
2. **Scan Dependencies**: 
    - Read the root `build.gradle` for buildscript dependencies (AGP, Kotlin).
    - Read the app-level `build.gradle` for implementation, kapt, and test dependencies.
3. **Lookup Versions**: Use the `version_lookup` tool to find the latest stable versions for all identified libraries.
4. **Format Output**: Provide a Markdown table with the following columns:
    - **Dependency**: The artifact name (e.g., `androidx.room:room-runtime`).
    - **Current Version**: The version currently defined in the Gradle files.
    - **Latest Stable**: The latest version returned by the lookup tool.
    - **Up to Date?**: A clear indicator (✅/❌) whether the project is on the latest version.
5. **Summarize**: Highlight key upgrades (like AGP or Kotlin) and any potential risks associated with being significantly out of date.
