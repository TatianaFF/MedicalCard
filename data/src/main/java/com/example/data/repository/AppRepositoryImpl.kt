package com.example.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.database.dao.AppDao
import com.example.database.models.NotificationEntity
import com.example.database.models.ProfileEntity
import com.example.database.models.VisitEntity
import com.example.models.Notification
import com.example.models.Profile
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor (
    private val dao: AppDao
) : AppRepository {
    override fun insertVisit(visit: Visit): Long = dao.insertVisit(visitToVisitEntity(visit))
    override suspend fun updateVisit(visit: Visit) = dao.updateVisit(visitToVisitEntity(visit))

    override suspend fun deleteVisitById(id: Long) = dao.deleteVisitById(id)

    override fun getVisits(): Flow<List<Visit>> =
        dao.getVisits()
            .map { visitsEntity ->
                visitsEntity.map { visitEntityToVisit(it) }
            }

    override fun getVisitById(id: Long): Flow<Visit> =
        dao.getVisitById(id)
            .map { visitsEntity ->
                visitEntityToVisit(visitsEntity)
        }

    override suspend fun insertNotification(notification: Notification) = dao.insertNotification(notificationToNotificationEntity(notification))
    override fun getNotificationsByIdVisit(idVisit: Long): Flow<List<Notification>> =
        dao.getNotificationsByIdVisit(idVisit)
            .map { notificationsEntity ->
                notificationsEntity.map { notificationEntityToNotification(it) }
        }

    override fun getVisitsByIdProfile(idProfile: Long): Flow<List<Visit>> =
        dao.getVisitsByIdProfile(idProfile)
            .map { visitsEntity ->
                visitsEntity.map { visitEntityToVisit(it) }
            }

    override suspend fun deleteNotificationById(id: Long) = dao.deleteNotificationById(id)
    override suspend fun updateNotification(notification: Notification) =  dao.updateNotification(notificationToNotificationEntity(notification))
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getPastNotifications(): Flow<List<Notification>> =
        dao.getPastNotifications()
            .map { notificationsEntity ->
                notificationsEntity.map { notificationEntityToNotification(it) }
            }

    override suspend fun insertProfile(profile: Profile) = dao.insertProfile(profileToProfileEntity(profile))

    override suspend fun deleteProfileById(id: Long) = dao.deleteProfileById(id)

    override suspend fun updateProfile(profile: Profile) = dao.updateProfile(profileToProfileEntity(profile))

    override fun getProfiles(): Flow<List<Profile>> =
        dao.getProfiles()
            .map {  profilesEntity ->
                profilesEntity.map { profileEntityToProfile(it) }
            }

    override fun getProfileById(id: Long): Flow<Profile> =
        dao.getProfileById(id)
            .map {
                profileEntityToProfile(it)
            }

    private fun visitToVisitEntity(visit: Visit) : VisitEntity = with(visit) {
        VisitEntity(id, name, date, comment, filesPaths, profileId)
    }

    private fun visitEntityToVisit(visitEntity: VisitEntity) : Visit = with(visitEntity) {
        Visit(id, name, date, comment, filesPaths, profileId)
    }

    private fun notificationToNotificationEntity(notification: Notification) : NotificationEntity = with(notification) {
        NotificationEntity(id, visitId, dateTime)
    }

    private fun notificationEntityToNotification(notificationEntity: NotificationEntity) : Notification = with(notificationEntity) {
        Notification(id, visitId, dateTime)
    }

    private fun profileToProfileEntity(profile: Profile) : ProfileEntity = with(profile) {
        ProfileEntity(id, name)
    }

    private fun profileEntityToProfile(profileEntity: ProfileEntity) : Profile = with(profileEntity) {
        Profile(id, name)
    }
}