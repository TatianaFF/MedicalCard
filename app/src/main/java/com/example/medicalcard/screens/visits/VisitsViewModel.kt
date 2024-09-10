package com.example.medicalcard.screens.visits

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetProfilesUseCase
import com.example.domain.GetVisitsUseCase
import com.example.medicalcard.adapters.ItemVisit
import com.example.medicalcard.utils.AppStatePrefs
import com.example.models.Profile
import com.example.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class VisitsViewModel @Inject constructor(
    private val appStatePrefs: AppStatePrefs,
    private val getVisitsUseCase: GetVisitsUseCase,
    private val getProfilesUseCase: GetProfilesUseCase
) : ViewModel() {

    init {
        fetchProfiles()
        fetchVisits()
    }

    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    val profiles = _profiles.asStateFlow()

    private fun fetchProfiles() {
        viewModelScope.launch {
            getProfilesUseCase.execute().collectLatest {
                _profiles.tryEmit(it)
            }
        }
    }

    private val _visitsDB = MutableStateFlow<List<Visit>?>(null)
    private val visitsDB = _visitsDB.asStateFlow()

    private fun fetchVisits() {
        viewModelScope.launch {
            getVisitsUseCase.execute().collectLatest { visits ->
                _visitsDB.tryEmit(visits.sortedBy { it.date }.asReversed())
            }
        }
    }

    private val _currentProfile: MutableStateFlow<Long?> = MutableStateFlow(appStatePrefs.currentProfile)
    val currentProfile = _currentProfile.asStateFlow()

    fun setCurrentProfile(id: Long?) {
        _currentProfile.tryEmit(id)
        appStatePrefs.currentProfile = id
    }

    fun getCurrentProfile() = appStatePrefs.currentProfile

    fun updateCurrentProfile() = _currentProfile.tryEmit(getCurrentProfile())

    private val _currentNameProfile: MutableStateFlow<String> = MutableStateFlow("")
    val currentNameProfile: StateFlow<String> = combine(
        currentProfile,
        _profiles
    ) { idCur, profiles ->
        var name = "Все"

        if (profiles.isNotEmpty() && idCur != null) name = profiles.first { p -> p.id == idCur }.name

        name

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _currentNameProfile.value
    )

    private val _searchText = MutableStateFlow("")
    private val searchText = _searchText.asStateFlow()

    fun setSearchText(text: String) {
        _searchText.tryEmit(text)
    }

    private val _filterDatePeriod: MutableStateFlow<Pair<LocalDate, LocalDate>?> = MutableStateFlow(null)
    val filterDatePeriod = _filterDatePeriod.asStateFlow()

    fun setFilterDatePeriod(period: Pair<LocalDate, LocalDate>?) {
        _filterDatePeriod.tryEmit(period)
    }

    val visits: StateFlow<List<Visit>?> = combine(
        searchText,
        filterDatePeriod,
        currentProfile,
        visitsDB
    ) { st, period, profile, list ->
        list?.let {
            val result: MutableList<Visit> = it.toMutableList()

            profile?.let { p -> result.removeIf { v -> v.profileId != p } }

            if (st.isNotBlank())
                result.removeIf { v -> !(v.name.uppercase().contains(st.trim().uppercase())) }

            period?.let {
                result.removeIf { v ->
                    val curDate = Instant.ofEpochSecond(v.date).atZone(ZoneOffset.UTC).toLocalDate()
                    !(curDate >= period.first && curDate <= period.second)
                }
            }

            result
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _visitsDB.value
    )
}