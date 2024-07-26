package com.example.medicalcard.screens.edit_visit

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DeleteVisitByIdUseCase
import com.example.domain.GetVisitByIdUseCase
import com.example.domain.InsertVisitUseCase
import com.example.domain.UpdateVisitUseCase
import com.example.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class VisitEditViewModel @Inject constructor(
    private val getVisitByIdUseCase: GetVisitByIdUseCase,
    private val insertVisitUseCase: InsertVisitUseCase,
    private val updateVisitUseCase: UpdateVisitUseCase,
    private val deleteVisitByIdUseCase: DeleteVisitByIdUseCase
) : ViewModel() {
    private val _nameVisit: MutableStateFlow<String?> = MutableStateFlow(null)
    val nameVisit = _nameVisit.asStateFlow()
    fun setNameVisit(name: String) {
        if (name.isNotBlank()) _nameVisit.tryEmit(name)
    }

    private val _commentVisit: MutableStateFlow<String?> = MutableStateFlow(null)
    val commentVisit = _commentVisit.asStateFlow()
    fun setCommentVisit(comment: String) {
        if (comment.isNotBlank()) _commentVisit.tryEmit(comment)
    }

    private val currentDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()
    ).withSecond(0).withNano(0)

    private val _dateTimeVisit: MutableStateFlow<LocalDateTime> = MutableStateFlow(currentDateTime)
    val dateTimeVisit = _dateTimeVisit.asStateFlow()
    fun setDateVisit(day: Int, month: Int, year: Int) {
        _dateTimeVisit.tryEmit(
            _dateTimeVisit.value
                .withDayOfMonth(day)
                .withMonth(month)
                .withYear(year)
        )
    }

    fun setTimeVisit(hours: Int, minutes: Int) {
        _dateTimeVisit.tryEmit(
            _dateTimeVisit.value
                .withHour(hours)
                .withMinute(minutes)
        )
    }

    private val _files: MutableStateFlow<List<Uri>> = MutableStateFlow(emptyList())
    val files = _files.asStateFlow()
    fun addFile(uri: Uri) {
        val newList = _files.value.toMutableList()
        newList.add(uri)
        _files.tryEmit(newList.toList())
    }

    fun deleteFile(uri: Uri) {
        val newList = _files.value.toMutableList()
        newList.remove(uri)
        _files.tryEmit(newList.toList())
    }

    fun insertVisit() {
        val date = dateTimeVisit.value.toEpochSecond(ZoneOffset.UTC)
        val name = requireNotNull(nameVisit.value) { "Необходимо заполнить название визита" }
        val newVisit = Visit(
            null,
            name,
            date,
            commentVisit.value,
            files.value.map { it.toString() }
        )
        viewModelScope.launch {
            insertVisitUseCase.execute(newVisit)
        }
    }

    fun updateVisit(id: Long) {
        val date = dateTimeVisit.value.toEpochSecond(ZoneOffset.UTC)
        val name = requireNotNull(nameVisit.value) { "Необходимо заполнить название визита" }
        val newVisit = Visit(
            id,
            name,
            date,
            commentVisit.value,
            files.value.map { it.toString() }
        )
        viewModelScope.launch {
            updateVisitUseCase.execute(newVisit)
        }
    }

    fun setDataVisit(id: Long) {
        viewModelScope.launch {
            getVisitByIdUseCase.execute(id)
                .onEach { visit ->
                    val date = LocalDateTime.ofEpochSecond(visit.date, 0, ZoneOffset.UTC)
                    setNameVisit(visit.name)
                    visit.comment?.let { setCommentVisit(it) }
                    setDateVisit(date.dayOfMonth, date.monthValue, date.year)
                    setTimeVisit(date.hour,date.minute)
                    visit.filesPaths?.forEach { addFile(it.toUri()) }
                }
                .catch {
                    throw Exception("Ошибка при обновлении данных визита")
                }
                .launchIn(viewModelScope)
        }
    }

    fun deleteVisitById(id: Long) {
        viewModelScope.launch {
            deleteVisitByIdUseCase.execute(id)
        }
    }
}