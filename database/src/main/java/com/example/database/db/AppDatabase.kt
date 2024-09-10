package com.example.database.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.database.dao.AppDao
import com.example.database.models.CategoryEntity
import com.example.database.models.NotificationEntity
import com.example.database.models.ProfileEntity
import com.example.database.models.VisitEntity
import com.example.database.utils.Converters

@Database(
    entities = [ProfileEntity::class, CategoryEntity::class, VisitEntity::class, NotificationEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun appDao(): AppDao
}