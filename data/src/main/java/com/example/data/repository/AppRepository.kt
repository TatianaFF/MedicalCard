package com.example.data.repository

import com.example.database.models.ProfileEntity
import com.example.models.Notification
import com.example.models.Profile
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun insertVisit(visit: Visit): Long

    suspend fun updateVisit(visit: Visit)

    suspend fun deleteVisitById(id: Long)

    fun getVisits(): Flow<List<Visit>>

    fun getVisitById(id: Long): Flow<Visit>

    suspend fun insertNotification(notification: Notification)

    fun getNotificationsByIdVisit(idVisit: Long): Flow<List<Notification>>

    fun getVisitsByIdProfile(idProfile: Long): Flow<List<Visit>>

    suspend fun deleteNotificationById(id: Long)

    suspend fun updateNotification(notification: Notification)

    fun getPastNotifications(): Flow<List<Notification>>

    suspend fun insertProfile(profile: Profile)

    suspend fun deleteProfileById(id: Long)

    suspend fun updateProfile(profile: Profile)

    fun getProfiles(): Flow<List<Profile>>

    fun getProfileById(id: Long): Flow<Profile>
}