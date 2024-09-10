package com.example.medicalcard.screens.edit_profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.DeleteProfileByIdUseCase
import com.example.domain.DeleteVisitByIdUseCase
import com.example.domain.GetProfileByIdUseCase
import com.example.domain.GetVisitsByIdProfileUseCase
import com.example.domain.InsertProfileUseCase
import com.example.domain.UpdateProfileUseCase
import com.example.medicalcard.utils.AppStatePrefs
import com.example.models.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel@Inject constructor(
    private val appStatePrefs: AppStatePrefs,
    private val insertProfileUseCase: InsertProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileByIdUseCase: DeleteProfileByIdUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val getVisitsByIdProfileUseCase: GetVisitsByIdProfileUseCase,
    private val deleteVisitByIdUseCase: DeleteVisitByIdUseCase
): ViewModel() {

    fun isFirstAppStart(): Boolean {
        return appStatePrefs.isFirstRun
    }

    fun setFirstAppStart(isFirst: Boolean) {
        appStatePrefs.isFirstRun = isFirst
    }

    fun setCurrentProfile(id: Long) {
        appStatePrefs.currentProfile = id
    }

    fun getCurrentProfile() = appStatePrefs.currentProfile

    private val _nameProfile: MutableStateFlow<String?> = MutableStateFlow(null)
    val nameProfile = _nameProfile.asStateFlow()
    fun setNameProfile(name: String) {
        if (name.isNotBlank()) _nameProfile.tryEmit(name)
    }

    fun fetchProfileData(id: Long) {
        viewModelScope.launch {
            val profile = getProfileByIdUseCase.execute(id).first()
            setNameProfile(profile.name)
        }
    }

    fun insertProfile() {
        val name = requireNotNull(nameProfile.value) { "Необходимо заполнить имя" }
        val newProfile = Profile(
            if (isFirstAppStart()) 0 else null,
            name
        )

        viewModelScope.launch {
            insertProfileUseCase.execute(newProfile)
        }

        setFirstAppStart(false)
    }

    fun updateProfile(id: Long) {
        val name = requireNotNull(nameProfile.value) { "Необходимо заполнить имя" }
        val newProfile = Profile(
            id,
            name
        )

        viewModelScope.launch {
            updateProfileUseCase.execute(newProfile)
        }
    }

    fun deleteProfileById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteProfileByIdUseCase.execute(id)

            getVisitsByIdProfileUseCase.execute(id).first().forEach { v ->
                deleteVisitByIdUseCase.execute(v.id!!)
            }
        }
    }
}