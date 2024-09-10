package com.example.models

data class Profile (
    val id: Long? = null,
    val name: String
//    val gender: Gender,
//    val dateOfBirth: Long?
) {
    enum class Gender(val genderString: String) {
        MALE("Мужчина"),
        FEMALE("Женщина")
    }
}