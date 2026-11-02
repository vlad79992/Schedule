package com.moshk.schedule.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moshk.schedule.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val repository: UserPreferencesRepository
) : ViewModel() {
    val groupName: StateFlow<String> = repository.groupNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = ""
    )

    val scheduleProvider: StateFlow<String> = repository.scheduleProviderFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = ""
    )

    fun updateGroupName(groupName: String) {
        viewModelScope.launch {
            repository.saveGroupName(groupName)
        }
    }

    fun updateProvider(provider: String) {
        viewModelScope.launch {
            repository.setScheduleProvider(provider)
        }
    }
}