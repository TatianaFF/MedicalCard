package com.example.medicalcard.screens.notify_dialog

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class NotifyDialogViewModel : ViewModel() {
    private val currentDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()
    ).withSecond(0).withNano(0)

    private val _dateTimeNotify: MutableStateFlow<LocalDateTime> = MutableStateFlow(currentDateTime)
    val dateTimeNotify = _dateTimeNotify.asStateFlow()
    fun setDateNotify(day: Int, month: Int, year: Int) {
        _dateTimeNotify.tryEmit(
            _dateTimeNotify.value
                .withDayOfMonth(day)
                .withMonth(month)
                .withYear(year)
        )
    }

    fun setTimeNotify(hours: Int, minutes: Int) {
        _dateTimeNotify.tryEmit(
            _dateTimeNotify.value
                .withHour(hours)
                .withMinute(minutes)
        )
    }
}