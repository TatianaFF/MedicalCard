package com.example.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.VisitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisit(visitEntity: VisitEntity): Long

    @Query("DELETE FROM Visit WHERE id = :id")
    suspend fun deleteVisitById(id: Long)

    @Update
    suspend fun updateVisit(visit: VisitEntity)

    @Query("SELECT * FROM Visit")
    fun getVisits(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM Visit WHERE id = :id")
    fun getVisitById(id: Long): Flow<VisitEntity>
}