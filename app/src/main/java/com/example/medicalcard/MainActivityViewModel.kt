package com.example.medicalcard

import androidx.lifecycle.ViewModel
import com.example.medicalcard.utils.AppStatePrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val appStatePrefs: AppStatePrefs
) : ViewModel() {
    fun isFirstAppStart(): Boolean {
        return appStatePrefs.isFirstRun
    }
}