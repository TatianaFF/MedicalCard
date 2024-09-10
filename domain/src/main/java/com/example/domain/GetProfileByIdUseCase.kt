package com.example.domain

import android.util.Log
import com.example.data.repository.AppRepository
import com.example.models.Profile
import com.example.models.Visit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileByIdUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    fun execute(id: Long): Flow<Profile> = appRepository.getProfileById(id)
}