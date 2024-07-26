package com.example.medicalcard.screens.visits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetVisitsUseCase
import com.example.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitsViewModel @Inject constructor(
    private val getVisitsUseCase: GetVisitsUseCase
) : ViewModel() {

    fun fetchVisits() {
        viewModelScope.launch {
            getVisitsUseCase.execute()
                .onEach {
                    _visits.value = it
                }
                .catch {
                    throw Exception("Ошибка при загрузке визитов")
                }
                .launchIn(viewModelScope)
        }
    }

    private val _visits = MutableStateFlow<List<Visit>>(emptyList())
//    val visits: StateFlow<VisitsUiState> = _visits

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    val visits = combine(
        searchText,
        _visits
    ) { st, list ->
        if (st.isBlank()) return@combine list

        val resultList =
            if (st.isNotBlank()) list.filter { v -> v.name.uppercase().contains(st.trim().uppercase()) } else list

        resultList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _visits.value
    )
}

//sealed class VisitsUiState {
//    data class Success(val visits: List<Visit>): VisitsUiState()
//    data class Error(val exception: Throwable): VisitsUiState()
//}