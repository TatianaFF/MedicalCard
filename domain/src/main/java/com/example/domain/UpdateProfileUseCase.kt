package com.example.domain

import com.example.data.repository.AppRepository
import com.example.models.Profile
import com.example.models.Visit
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(profile: Profile) = appRepository.updateProfile(profile)
}