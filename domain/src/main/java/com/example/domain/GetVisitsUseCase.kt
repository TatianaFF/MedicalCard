package com.example.domain

import com.example.data.repository.AppRepository
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVisitsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    fun execute(): Flow<List<Visit>> = appRepository.getVisits()
}