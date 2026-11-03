package com.moshk.schedule.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moshk.schedule.data.ScheduleGroup
import com.moshk.schedule.data.ScheduleRepository
import com.moshk.schedule.data.UserPreferencesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val repository: UserPreferencesRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    val groupName: StateFlow<String> = repository.groupNameFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = ""
    )

    val groups: StateFlow<List<ScheduleGroup>> field = MutableStateFlow(emptyList())
    val isLoadingGroups: StateFlow<Boolean> field = MutableStateFlow(false)
    val groupsError: StateFlow<String?> field = MutableStateFlow(null)
    val saveError: StateFlow<String?> field = MutableStateFlow(null)

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            isLoadingGroups.value = true
            groupsError.value = null
            try {
                groups.value = scheduleRepository.getGroups()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                groupsError.value = e.message ?: "Не удалось загрузить список групп"
            } finally {
                isLoadingGroups.value = false
            }
        }
    }

    suspend fun selectGroup(groupName: String): Boolean {
        saveError.value = null
        return try {
            repository.saveGroupName(groupName)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            saveError.value = e.message ?: "Не удалось сохранить группу"
            false
        }
    }
}