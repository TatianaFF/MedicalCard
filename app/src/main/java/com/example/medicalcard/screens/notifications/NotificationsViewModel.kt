package com.example.medicalcard.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DeleteNotificationByIdUseCase
import com.example.domain.GetPastNotificationsUseCase
import com.example.domain.GetVisitByIdUseCase
import com.example.models.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getPastNotificationsUseCase: GetPastNotificationsUseCase,
    private val deleteNotificationByIdUseCase: DeleteNotificationByIdUseCase,
    private val getVisitByIdUseCase: GetVisitByIdUseCase
) : ViewModel() {

    private val _notifications: MutableStateFlow<List<Pair<Notification, String>>?> = MutableStateFlow(null)
    val notifications = _notifications.asStateFlow()

    init {
        fetchPastNotifications()
    }

    private fun fetchPastNotifications() {
        viewModelScope.launch {
            getPastNotificationsUseCase.execute().collectLatest { list ->
                val notifyVisitName = list.map {
                    val visitName = getVisitByIdUseCase.execute(it.visitId).first().name
                    Pair(first = it, second = visitName)
                }

                _notifications.tryEmit(notifyVisitName)
            }
        }
    }

    fun deleteNotifications() {
        viewModelScope.launch {
            notifications.value?.forEach {
                deleteNotificationByIdUseCase.execute(requireNotNull(it.first.id))
            }
        }
    }
}