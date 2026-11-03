package com.moshk.schedule.ui.screens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moshk.schedule.data.ScheduleGroup
import com.moshk.schedule.data.ScheduleRepository
import com.moshk.schedule.data.UserPreferencesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchScreenViewModel(
    private val repository: ScheduleRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val groups: StateFlow<List<ScheduleGroup>> field = MutableStateFlow(emptyList())
    val isLoading: StateFlow<Boolean> field = MutableStateFlow(false)
    val error: StateFlow<String?> field = MutableStateFlow(null)

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                groups.value = repository.getGroups()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error.value = e.message ?: "Не удалось загрузить список групп"
            } finally {
                isLoading.value = false
            }
        }
    }

    suspend fun selectGroup(groupName: String): Boolean =
        try {
            preferencesRepository.saveGroupName(groupName)
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            error.value = e.message ?: "Не удалось сохранить группу"
            false
        }
}