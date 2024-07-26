package com.example.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.database.utils.Converters

@Entity(tableName = "visit")
data class VisitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
//    val categoryId: Long? = null,
    val date: Long,
    val comment: String? = null,
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "file_paths")
    val filesPaths: List<String>? = null
)