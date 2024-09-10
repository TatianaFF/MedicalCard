package com.example.domain

import com.example.data.repository.AppRepository
import com.example.models.Notification
import com.example.models.Visit
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(notification: Notification) = appRepository.updateNotification(notification)
}