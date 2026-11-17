# Skill: Add Item to Overflow Menu

## Objective
Provide a clear, step-by-step guide for adding new items to the overflow menu (three dots) in PracticeTime fragments, ensuring the bottom navigation bar maintains a maximum of 5 items to comply with Material Design guidelines.

## Constraints
1. **Branch Check**: Before making any changes, verify the current branch using `git branch --show-current`. For feature work, use dedicated branches (e.g., `notes-feature`). **DO NOT** work on master for feature development.
2. **Bottom Navigation Limit**: The bottom navigation bar (`bottom_navigation_menu.xml`) **MUST NOT** exceed 5 items. If adding a new feature that users expect in navigation, consider:
   - Replacing an existing bottom nav item (least critical)
   - Adding to an overflow menu instead (preferred for secondary features)
3. **File Patterns**: Follow existing naming conventions:
   - Menu items: `*_toolbar_menu_base.xml`
   - Menu item IDs: `<fragmentName>Toolbar<Feature>`
   - Fragments: `PascalCaseFragment.kt` in `ui/<feature>/`
   - Layouts: `fragment_<feature>.xml`
   - Icons: `ic_<feature>.xml` in `res/drawable/`
   - Strings: `navigation<Feature>Title` in `res/values/strings.xml`

## Instructions

### Step 1: Verify Branch
```bash
cd /path/to/PracticeTime
git branch --show-current
```
Ensure you're on the correct feature branch (e.g., `notes-feature`). **Never make changes on master.**

### Step 2: Decide Visibility Scope
Choose one of the following:

**Option A: Single Fragment** (Recommended for feature-specific items)
- Add to only the most relevant fragment's menu
- Example: Add "Archived Goals" only to Goals fragment

**Option B: All Fragments** (For globally accessible features like Notes)
- Add to all four bottom navigation fragments' menus:
  - `sessions_list_menu_base.xml`
  - `goals_toolbar_menu_base.xml`
  - `library_toolbar_menu_base.xml`
  - `stats_toolbar_menu_base.xml`
- Use different IDs for each: `<fragmentName>Toolbar<Feature>`
- Example: Notes appears in all overflow menus

### Step 3: Add Menu Item(s)
Edit the appropriate `_toolbar_menu_base.xml` file(s):

**For Single Fragment:**
```xml
<!-- app/src/main/res/menu/<target>_toolbar_menu_base.xml -->
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/<fragmentName>Toolbar<Feature>"
        android:enabled="true"
        android:icon="@drawable/ic_<feature>"
        android:title="@string/navigation<Feature>Title"
        app:showAsAction="never" />

</menu>
```

**For All Fragments:**
```xml
<!-- app/src/main/res/menu/sessions_list_menu_base.xml -->
<item android:id="@+id/sessionsListToolbarNotes" ... />

<!-- app/src/main/res/menu/goals_toolbar_menu_base.xml -->
<item android:id="@+id/goalsToolbarNotes" ... />

<!-- app/src/main/res/menu/library_toolbar_menu_base.xml -->
<item android:id="@+id/libraryToolbarNotes" ... />

<!-- app/src/main/res/menu/stats_toolbar_menu_base.xml -->
<item android:id="@+id/statsToolbarNotes" ... />
```

**Key Attributes:**
- `android:id`: **Unique ID per fragment**, follow pattern `<fragmentName>Toolbar<Feature>`
- `android:icon`: Reference to your icon drawable
- `android:title`: Reference to your string resource
- `app:showAsAction="never"`: Ensures it appears in overflow, not on the toolbar

### Step 4: Add String Resource
Edit `app/src/main/res/values/strings.xml`:

```xml
<string name="navigation<Feature>Title">Feature Name</string>
```

### Step 5: Create Icon
Create a new vector drawable at `app/src/main/res/drawable/ic_<feature>.xml`:

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
  <path
      android:fillColor="@color/md_purple_A700"
      android:pathData="M4,4H20V20H4Z"/>
</vector>
```

**Notes:**
- Use `@color/md_purple_A700` for consistency with other icons
- Keep pathData simple for clarity at small sizes (24x24dp)
- Test visibility at small sizes

### Step 6: Create Fragment (if needed)
If the feature requires a new screen, create:

**File:** `app/src/main/java/de/practicetime/practicetime/ui/<feature>/<Feature>Fragment.kt`

```kotlin
package de.practicetime.practicetime.ui.<feature>

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.practicetime.practicetime.R

class <Feature>Fragment : Fragment(R.layout.fragment_<feature>) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // IMPORTANT: Setup back button for fragments not in bottom nav
        val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.<feature>Toolbar)
        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_nav_back)
        }
        
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
}
```

**Critical Note:** If your fragment is NOT in the bottom navigation menu, **you MUST add a back button** as shown above, otherwise users cannot navigate back.

### Step 7: Create Layout (if needed)
Create the layout file at `app/src/main/res/layout/fragment_<feature>.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".ui.<feature>.<Feature>Fragment">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/<feature>Appbar"
        style="@style/AppBar">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:id="@+id/<feature>_collapsing_toolbar_layout"
            style="@style/CollapsingToolbar"
            app:title="@string/navigation<Feature>Title"
            app:toolbarId="@+id/<feature>Toolbar">

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/<feature>Toolbar"
                style="@style/Toolbar" />

        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:gravity="center"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
        
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Feature coming soon"
            android:textSize="18sp" />
    </LinearLayout>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### Step 8: Add to Navigation Graph (if needed)
Edit `app/src/main/res/navigation/nav_graph.xml`:

```xml
<fragment
    android:id="@+id/<feature>Fragment"
    android:name="de.practicetime.practicetime.ui.<feature>.<Feature>Fragment"
    android:label="<Feature>" />
```

### Step 9: Add Click Handler(s)
In **each target fragment**, update the toolbar setup function:

**For SessionListFragment:**
```kotlin
// Add import
import androidx.navigation.fragment.findNavController

// Update resetToolbar
private fun resetToolbar() {
    sessionListToolbar.apply {
        menu?.clear()
        inflateMenu(R.menu.sessions_list_menu_base)
        setCommonToolbar(requireActivity(), this) { itemId ->
            when (itemId) {
                R.id.sessionsListToolbarNotes -> {
                    findNavController().navigate(R.id.notesFragment)
                }
            }
        }
        navigationIcon = null
    }
}
```

**For GoalsFragment:**
```kotlin
// Add import
import androidx.navigation.fragment.findNavController

// Update resetToolbar
private fun resetToolbar() {
    goalsToolbar.apply {
        menu?.clear()
        inflateMenu(R.menu.goals_toolbar_menu_base)
        setCommonToolbar(requireActivity(), this) { itemId ->
            when (itemId) {
                R.id.goalsToolbarArchivedGoals -> {
                    val i = Intent(requireActivity(), ArchivedGoalsActivity::class.java)
                    startActivity(i)
                }
                R.id.goalsToolbarNotes -> {
                    findNavController().navigate(R.id.notesFragment)
                }
            }
        }
        lifecycleScope.launch {
            menu.findItem(R.id.goalsToolbarArchivedGoals).title = requireActivity().getString(
                R.string.archivedGoalsToolbar
            ).format(
                PracticeTime.goalDescriptionDao.getArchivedWithCategories().size
            )
        }
        navigationIcon = null
    }
}
```

**For LibraryFragment:**
```kotlin
// Add import
import androidx.navigation.fragment.findNavController

// Update resetToolbar
private fun resetToolbar() {
    libraryToolbar.apply {
        menu?.clear()
        setCommonToolbar(requireActivity(), this) { itemId ->
            when (itemId) {
                R.id.libraryToolbarNotes -> {
                    findNavController().navigate(R.id.notesFragment)
                }
            }
        }
        inflateMenu(R.menu.library_toolbar_menu_base)
        navigationIcon = null
    }
}
```

**For StatisticsOverviewFragment:**
```kotlin
// Add import
import androidx.navigation.fragment.findNavController

// Update setCommonToolbar call
setCommonToolbar(requireActivity(), view.findViewById(R.id.statsToolbar)) { itemId ->
    when (itemId) {
        R.id.statsToolbarNotes -> {
            findNavController().navigate(R.id.notesFragment)
        }
    }
}
```

### Step 10: Verify Changes
1. **Build**: Ensure `./gradlew clean assembleDebug` passes
2. **Test**: Verify the new item appears in the overflow menu of the target fragment(s)
3. **Navigation**: Click the item and verify:
   - It navigates correctly to the target fragment
   - The back button works (if applicable)
   - No crashes occur

## Example: Adding "Notes" Feature to All Fragments

### Files Modified:

**1. Menu Files (4 files):**
```xml
<!-- sessions_list_menu_base.xml -->
<item android:id="@+id/sessionsListToolbarNotes" ... />

<!-- goals_toolbar_menu_base.xml -->
<item android:id="@+id/goalsToolbarNotes" ... />

<!-- library_toolbar_menu_base.xml -->
<item android:id="@+id/libraryToolbarNotes" ... />

<!-- stats_toolbar_menu_base.xml -->
<item android:id="@+id/statsToolbarNotes" ... />
```

**2. String Resource:**
```xml
<string name="navigationNotesTitle">Notes</string>
```

**3. Icon:**
```xml
<!-- ic_notes.xml -->
<vector ...>
  <path android:fillColor="@color/md_purple_A700" ... />
</vector>
```

**4. Fragment:**
- Created `NotesFragment.kt` in `ui/notes/`
- Created `fragment_notes.xml`
- Added to `nav_graph.xml`

**5. Click Handlers (4 fragments):**
- SessionListFragment
- GoalsFragment
- LibraryFragment
- StatisticsOverviewFragment

**6. Back Button:**
- Added to NotesFragment toolbar setup

## Common Pitfalls
- **Bottom Nav Overflow**: NEVER exceed 5 items in `bottom_navigation_menu.xml`
- **ID Conflicts**: Use **unique IDs per fragment** following `<fragmentName>Toolbar<Feature>` pattern
- **Missing Imports**: Forgetting `import androidx.navigation.fragment.findNavController`
- **Empty Lambda**: Forgetting to add the click handler in `setCommonToolbar`
- **Navigation Mismatch**: Fragment ID in menu must match ID in nav_graph.xml
- **No Back Button**: Forgetting to add back button for fragments not in bottom nav (causes user to be stuck)

## Quick Checklist
- [ ] Verified branch (not master)
- [ ] Bottom nav has ≤5 items
- [ ] Menu item(s) added to correct `_toolbar_menu_base.xml` file(s)
- [ ] String resource added
- [ ] Icon created (24x24dp, purple color)
- [ ] Fragment created (if needed)
- [ ] Layout created (if needed)
- [ ] **Back button added to fragment toolbar** (if fragment not in bottom nav)
- [ ] Fragment added to nav_graph (if needed)
- [ ] Click handler added to each target fragment
- [ ] Build passes
- [ ] Feature works in app
