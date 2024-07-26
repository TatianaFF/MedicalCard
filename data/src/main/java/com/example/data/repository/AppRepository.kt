package com.example.data.repository

import com.example.models.Visit
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun insertVisit(visit: Visit): Long

    suspend fun updateVisit(visit: Visit)

    suspend fun deleteVisitById(id: Long)

    fun getVisits(): Flow<List<Visit>>

    fun getVisitById(id: Long): Flow<Visit>

}