package com.example.domain

import com.example.data.repository.AppRepository
import com.example.models.Visit
import javax.inject.Inject

class UpdateVisitUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(visit: Visit) = appRepository.updateVisit(visit)
}