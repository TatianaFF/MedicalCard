package com.example.domain

import android.util.Log
import com.example.data.repository.AppRepository
import com.example.models.Notification
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVisitsByIdProfileUseCase@Inject constructor(
    private val appRepository: AppRepository
) {
    fun execute(idProfile: Long): Flow<List<Visit>> = appRepository.getVisitsByIdProfile(idProfile)
}