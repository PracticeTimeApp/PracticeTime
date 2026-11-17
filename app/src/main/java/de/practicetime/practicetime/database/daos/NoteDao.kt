/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.database.daos

import androidx.room.Dao
import androidx.room.Query
import de.practicetime.practicetime.database.BaseDao
import de.practicetime.practicetime.database.entities.Note

@Dao
abstract class NoteDao : BaseDao<Note>(tableName = "note") {

    @Query("SELECT * FROM note WHERE profile_id = 0 ORDER BY modified_at DESC")
    protected abstract suspend fun getAllInternal(): List<Note>

    /**
     * Get all notes for the current profile
     */
    override suspend fun getAll(): List<Note> = getAllInternal()

    @Query("SELECT * FROM note WHERE id = :id AND profile_id = 0")
    protected abstract suspend fun getInternal(id: Long): Note?

    /**
     * Get a single note by ID
     */
    override suspend fun get(id: Long): Note? = getInternal(id)

    /**
     * Check if a title already exists for the current profile
     */
    @Query("SELECT COUNT(*) FROM note WHERE title = :title AND profile_id = 0 AND id != :excludeId")
    abstract suspend fun titleExists(title: String, excludeId: Long = 0): Int

    /**
     * Delete all notes for the current profile
     */
    @Query("DELETE FROM note WHERE profile_id = 0")
    abstract suspend fun deleteAll(): Int

    /**
     * Get the count of notes for the current profile
     */
    @Query("SELECT COUNT(*) FROM note WHERE profile_id = 0")
    abstract suspend fun count(): Int
}
