package com.example.domain

import com.example.data.repository.AppRepository
import javax.inject.Inject

class DeleteVisitByIdUseCase @Inject constructor (
    private val appRepository: AppRepository
) {
    suspend fun execute(id: Long) = appRepository.deleteVisitById(id)
}