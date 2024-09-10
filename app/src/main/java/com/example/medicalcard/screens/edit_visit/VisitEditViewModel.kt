package com.example.medicalcard.screens.edit_visit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DeleteNotificationByIdUseCase
import com.example.domain.DeleteVisitByIdUseCase
import com.example.domain.GetNotificationsByIdVisitUseCase
import com.example.domain.GetVisitByIdUseCase
import com.example.domain.InsertNotificationUseCase
import com.example.domain.InsertVisitUseCase
import com.example.domain.UpdateNotificationUseCase
import com.example.domain.GetProfilesUseCase
import com.example.domain.UpdateVisitUseCase
import com.example.medicalcard.alarm.AlarmItem
import com.example.medicalcard.alarm.AlarmSchedulerImpl
import com.example.models.Notification
import com.example.models.Profile
import com.example.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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
    @ApplicationContext private val context: Context,
    private val getVisitByIdUseCase: GetVisitByIdUseCase,
    private val insertVisitUseCase: InsertVisitUseCase,
    private val updateVisitUseCase: UpdateVisitUseCase,
    private val deleteVisitByIdUseCase: DeleteVisitByIdUseCase,
    private val insertNotificationUseCase: InsertNotificationUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase,
    private val getNotificationsByIdVisitUseCase: GetNotificationsByIdVisitUseCase,
    private val deleteNotificationByIdUseCase: DeleteNotificationByIdUseCase,
    private val getProfilesUseCase: GetProfilesUseCase,
) : ViewModel() {
    private val currentVisit: MutableStateFlow<Visit?> = MutableStateFlow(null)

    private val _profiles: MutableStateFlow<List<Profile>?> = MutableStateFlow(null)
    val profiles = _profiles.asStateFlow()

    private fun setProfiles(profiles: List<Profile>) {
        _profiles.tryEmit(profiles)
    }

    private val _profileVisit: MutableStateFlow<Profile?> = MutableStateFlow(null)
    val profileVisit = _profileVisit.asStateFlow()

    fun setProfileVisit(profile: Profile) {
        _profileVisit.tryEmit(profile)
    }

    init {
        fetchProfiles()
    }

    fun initData(id: Long) {
        viewModelScope.launch {
            profiles.collectLatest { fetchDataVisit(id) }
            fetchNotifications(id)
        }
    }

    private fun fetchProfiles() {
        viewModelScope.launch {
            getProfilesUseCase.execute().first().apply { setProfiles(this) }
        }
    }

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

    private val _dateTimeNotifications: MutableStateFlow<List<Pair<Long?, LocalDateTime>>> = MutableStateFlow(emptyList())
    val dateTimeNotifications = _dateTimeNotifications.asStateFlow()
    fun addDateTimeNotify(id: Long? = null, dateTime: LocalDateTime) {
        val newList = _dateTimeNotifications.value.toMutableList()
        newList.add(Pair(id, dateTime))
        _dateTimeNotifications.tryEmit(newList.toList())
    }

    fun deleteDateTimeNotifyFromState(index: Int) {
        val newList = _dateTimeNotifications.value.toMutableList()
        newList.removeAt(index)
        _dateTimeNotifications.tryEmit(newList.toList())
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
            files.value.map { it.toString() },
            requireNotNull(profileVisit.value?.id)
        )

        viewModelScope.launch(Dispatchers.IO) {
            val newIdVisit = insertVisitUseCase.execute(newVisit)
            updateNotifications(newIdVisit)
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
            files.value.map { it.toString() },
            requireNotNull(profileVisit.value?.id)
        )

        viewModelScope.launch {
            updateVisitUseCase.execute(newVisit)
            updateNotifications(id)
        }
    }

    private fun updateNotifications(idVisit: Long) {
        viewModelScope.launch {
            dateTimeNotifications.value.forEach { ldt ->
                val newNotification = Notification(
                    ldt.first,
                    idVisit,
                    ldt.second.toEpochSecond(ZoneOffset.UTC)
                )
                if (ldt.first == null) {
                    insertNotificationUseCase.execute(newNotification)

                    val alarmScheduler = AlarmSchedulerImpl(context)
                    alarmScheduler.schedule(
                        AlarmItem(
                        alarmTime = ldt.second,
                        message = requireNotNull(currentVisit.value?.name)
                    ))
                }
                else
                    updateNotificationUseCase.execute(newNotification)
            }
        }
    }

    private fun fetchDataVisit(id: Long) {
        viewModelScope.launch {
            val visit = getVisitByIdUseCase.execute(id).first()
            currentVisit.tryEmit(visit)

            val date = LocalDateTime.ofEpochSecond(visit.date, 0, ZoneOffset.UTC)
            setNameVisit(visit.name)
            visit.comment?.let { setCommentVisit(it) }
            setDateVisit(date.dayOfMonth, date.monthValue, date.year)
            setTimeVisit(date.hour,date.minute)
            visit.filesPaths?.forEach { addFile(it.toUri()) }

            profiles.value?.let { profiles -> setProfileVisit(profiles.first { it.id == visit.profileId }) }
        }
    }

    private fun fetchNotifications(idVisit: Long) {
        viewModelScope.launch {
            getNotificationsByIdVisitUseCase.execute(idVisit).first().forEach {
                val date = LocalDateTime.ofEpochSecond(it.dateTime, 0, ZoneOffset.UTC)
                if (!dateTimeNotifications.value.contains(Pair(it.id, date)))
                    addDateTimeNotify(it.id, date)
            }
        }
    }

    fun deleteVisitById(id: Long) {
        viewModelScope.launch {
            deleteVisitByIdUseCase.execute(id)
        }
    }

    fun deleteNotification(pair: Pair<Long, LocalDateTime>) {
        viewModelScope.launch {
            deleteNotificationByIdUseCase.execute(pair.first)

            val alarmScheduler = AlarmSchedulerImpl(context)

            alarmScheduler.cancel(
                AlarmItem(
                    alarmTime = pair.second,
                    message = requireNotNull(currentVisit.value?.name)
                )
            )
        }
    }
}