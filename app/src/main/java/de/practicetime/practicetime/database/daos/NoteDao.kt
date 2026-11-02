/*
 * This software is licensed under the MIT license
 *
 * Copyright (c) 2022, Javier Carbone, author Michael Prommersberger
 * Additions and modifications, author Matthias Emde
 */

package de.practicetime.practicetime.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.practicetime.practicetime.database.BaseDao
import de.practicetime.practicetime.database.entities.Note

@Dao
abstract class NoteDao : BaseDao<Note>(tableName = "note") {

    /**
     * Get all notes for the current profile
     */
    @Query("SELECT * FROM note WHERE profile_id = 0 ORDER BY modified_at DESC")
    override suspend fun getAll(): List<Note>

    /**
     * Get a single note by ID
     */
    @Query("SELECT * FROM note WHERE id = :id AND profile_id = 0")
    override suspend fun get(id: Long): Note?

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
