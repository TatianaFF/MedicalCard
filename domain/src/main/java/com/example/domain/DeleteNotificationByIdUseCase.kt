package com.example.domain

import com.example.data.repository.AppRepository
import javax.inject.Inject

class DeleteNotificationByIdUseCase @Inject constructor (
    private val appRepository: AppRepository
) {
    suspend fun execute(id: Long) = appRepository.deleteNotificationById(id)
}