package com.example.medicalcard.utils

import java.time.LocalDateTime

fun getDateString(date: LocalDateTime): String {
    val days = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val months = arrayOf(
        "Января",
        "Февраля",
        "Марта",
        "Апреля",
        "Мая",
        "Июня",
        "Июля",
        "Августа",
        "Сентября",
        "Октября",
        "Ноября",
        "Декабря"
    )
    return "${days[date.dayOfWeek.value - 1]}, ${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year} г."
}

fun getTimeString(date: LocalDateTime): String {
    val hours: String =
        if (date.hour.toString().length == 1) "0${date.hour}" else date.hour.toString()
    val minutes: String =
        if (date.minute.toString().length == 1) "0${date.minute}" else date.minute.toString()
    return "$hours:$minutes"
}