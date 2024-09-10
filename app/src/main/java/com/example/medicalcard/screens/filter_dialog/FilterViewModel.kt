package com.example.medicalcard.screens.filter_dialog

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class FilterViewModel : ViewModel() {
    private val currentDate = LocalDate.now()

    private val _dateFrom: MutableStateFlow<LocalDate> = MutableStateFlow(currentDate)
    val dateFrom = _dateFrom.asStateFlow()
    fun setDateFrom(dateFrom: LocalDate) {
        _dateFrom.tryEmit(dateFrom)
    }

    private val _dateTo: MutableStateFlow<LocalDate> = MutableStateFlow(currentDate)
    val dateTo = _dateTo.asStateFlow()
    fun setDateTo(dateTo: LocalDate) {
        _dateTo.tryEmit(dateTo)
    }
}