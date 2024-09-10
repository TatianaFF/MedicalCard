package com.example.medicalcard.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val FILE_NAME = "appState.pref"
private const val IS_FIRST_RUN = "isFirstRun"
private const val ID_CURRENT_PROFILE = "ID_CURRENT_PROFILE"

@Singleton
class AppStatePrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    private var _isFirstRun: Boolean = sharedPreferences.getBoolean(IS_FIRST_RUN, true)
    private var _currentProfile: Long? = sharedPreferences.getLong(ID_CURRENT_PROFILE, -1)

    var isFirstRun: Boolean
        @Synchronized
        get() = _isFirstRun
        @Synchronized
        set(value) {
            _isFirstRun = value
            sharedPreferences.edit()
                .putBoolean(IS_FIRST_RUN, value)
                .apply()
        }

    var currentProfile: Long?
        @Synchronized
        get() = if (_currentProfile == -1L) null else _currentProfile
        @Synchronized
        set(value) {
            _currentProfile = value
            if (value == null) {
                sharedPreferences.edit()
                    .remove(ID_CURRENT_PROFILE)
                    .apply()
            } else {
                sharedPreferences.edit()
                    .putLong(ID_CURRENT_PROFILE, value)
                    .apply()
            }
        }
}