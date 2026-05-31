package com.example.airline.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardingPassDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pass: BoardingPassEntity)

    @Query("SELECT * FROM boarding_passes ORDER BY savedAt DESC")
    fun getAllFlow(): Flow<List<BoardingPassEntity>>

    @Query("SELECT * FROM boarding_passes ORDER BY savedAt DESC")
    suspend fun getAll(): List<BoardingPassEntity>

    @Query("DELETE FROM boarding_passes")
    suspend fun deleteAll()
}
