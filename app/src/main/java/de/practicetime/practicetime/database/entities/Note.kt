/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import de.practicetime.practicetime.database.ModelWithTimestamps

/**
 * Entity representing a user note.
 * 
 * Database constraints:
 * - title: limited to 100 characters to prevent database issues
 * - content: limited to 5000 characters to prevent database issues
 * - profile_id: for multi-profile support (currently always 0)
 */
@Entity(tableName = "note")
data class Note (
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "profile_id", index = true) val profileId: Int = 0,
) : ModelWithTimestamps() {
    
    companion object {
        const val MAX_TITLE_LENGTH = 100
        const val MAX_CONTENT_LENGTH = 5000
    }
}
