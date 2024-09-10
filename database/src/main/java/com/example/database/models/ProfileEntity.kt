package com.example.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
//    val gender: Gender = Gender.FEMALE,
//    val dateOfBirth: Long? = null
) {
    enum class Gender(val genderString: String) {
        MALE("Мужчина"),
        FEMALE("Женщина")
    }
}