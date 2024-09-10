package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class NotificationEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val visitId: Long,
    val dateTime: Long
)