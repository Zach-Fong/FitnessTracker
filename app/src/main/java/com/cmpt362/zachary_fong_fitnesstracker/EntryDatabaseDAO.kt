package com.cmpt362.zachary_fong_fitnesstracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDatabaseDAO {
    @Insert
    suspend fun insertEntry(entry: Entry)

    @Query("SELECT * FROM entry_table")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entry_table WHERE id = :id")
    suspend fun getEntry(id: Long): Entry

    @Query("DELETE FROM entry_table WHERE id = :key")
    suspend fun deleteEntry(key: Long)
}