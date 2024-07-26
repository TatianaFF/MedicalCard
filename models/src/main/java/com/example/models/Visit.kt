package com.example.models

data class Visit (
    val id: Long? = null,
    val name: String,
    val date: Long,
    val comment: String?,
    val filesPaths: List<String>?
)