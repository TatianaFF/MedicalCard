package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = ""
)