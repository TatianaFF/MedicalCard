package com.example.medicalcard.utils

import java.time.LocalDate
import java.time.LocalDateTime

private val months = arrayOf(
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

private val days = arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")

fun getDateWithDayWeekString(date: LocalDate): String = "${days[date.dayOfWeek.value - 1]}, ${getDateString(date)}"

fun getDateShort(date: LocalDate): String = "${formatWithZero(date.dayOfMonth)}.${formatWithZero(date.monthValue)}.${formatWithZero(date.year)} г."

fun getTimeString(date: LocalDateTime): String = "${formatWithZero(date.hour)}:${formatWithZero(date.minute)}"

fun getDateString(date: LocalDate): String = "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year} г."
private fun formatWithZero(num: Int): String = if (num.toString().length == 1) "0$num" else "$num"