package com.example.medicalcard.screens.profile_dialog

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.domain.GetProfilesUseCase
import com.example.domain.GetVisitsUseCase
import com.example.medicalcard.utils.AppStatePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ProfileDialogViewModel : ViewModel() {
    private val _currentProfile: MutableStateFlow<Long?> = MutableStateFlow(null)
    val currentProfile = _currentProfile.asStateFlow()
    fun setCurrentProfile(id: Long?) {
        _currentProfile.tryEmit(id)
    }
}