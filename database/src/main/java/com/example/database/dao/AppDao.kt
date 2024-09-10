package com.example.database.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.database.models.CategoryEntity
import com.example.database.models.NotificationEntity
import com.example.database.models.ProfileEntity
import com.example.database.models.VisitEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Dao
interface AppDao {
    @Insert
    suspend fun insertProfile(profileEntity: ProfileEntity)

    @Query("DELETE FROM Profile WHERE id = :id")
    suspend fun deleteProfileById(id: Long)

    @Update
    suspend fun updateProfile(profileEntity: ProfileEntity)

    @Query("SELECT * FROM Profile")
    fun getProfiles(): Flow<List<ProfileEntity>>

    @Insert
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM Category")
    fun getCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM Category WHERE id = :id")
    fun getCategoryById(id: Long): Flow<CategoryEntity>

    @Insert
    fun insertVisit(visitEntity: VisitEntity): Long

    @Query("DELETE FROM Visit WHERE id = :id")
    suspend fun deleteVisitById(id: Long)

    @Update
    suspend fun updateVisit(visit: VisitEntity)

    @Query("SELECT * FROM Visit")
    fun getVisits(): Flow<List<VisitEntity>>

    @Query("SELECT * FROM Visit WHERE id = :id")
    fun getVisitById(id: Long): Flow<VisitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notificationEntity: NotificationEntity)

    @Update
    suspend fun updateNotification(notificationEntity: NotificationEntity)

    @Query("DELETE FROM Notification WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("SELECT * FROM Notification")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM Notification WHERE id = :id")
    fun getNotificationById(id: Long): Flow<NotificationEntity>

    @Query("SELECT * FROM Notification WHERE visitId = :idVisit")
    fun getNotificationsByIdVisit(idVisit: Long): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM Visit WHERE profile_id = :idProfile")
    fun getVisitsByIdProfile(idProfile: Long): Flow<List<VisitEntity>>

    @RequiresApi(Build.VERSION_CODES.O)
//    @Query("SELECT * FROM Notification WHERE datetime(Notification.dateTime/1000,'unixepoch','localtime') <= datetime('now', 'localtime')")
    @Query("SELECT * FROM Notification WHERE datetime(dateTime, 'unixepoch') <= datetime('now', 'localtime') ")
    fun getPastNotifications(): Flow<List<NotificationEntity>>

//    @Query("UPDATE Notification SET notified = :notified WHERE id = :id")
//    fun updateNotifiedNotification(id: Long, notified: Boolean)

    @Query("SELECT * FROM profile WHERE id = :id")
    fun getProfileById(id: Long): Flow<ProfileEntity>

}