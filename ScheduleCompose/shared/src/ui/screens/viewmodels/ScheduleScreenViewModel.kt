package com.moshk.schedule.ui.screens.viewmodels

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moshk.schedule.data.DaySchedule
import com.moshk.schedule.data.ScheduleRepository
import com.moshk.schedule.data.ScheduleEvent
import com.moshk.schedule.data.UserPreferencesRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class ScheduleScreenViewModel(
    val groupName: String?,
    private val repository: ScheduleRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val schedule: StateFlow<Map<LocalDate, DaySchedule>?> field = MutableStateFlow(null)
    val isLoading: StateFlow<Boolean> field = MutableStateFlow(false)
    val error: StateFlow<String?> field = MutableStateFlow(null)
    val activeGroupName: StateFlow<String?> field =
        MutableStateFlow(groupName?.takeIf { it.isNotBlank() })
    val selectedEvent: StateFlow<ScheduleEvent?> field = MutableStateFlow(null)
    private var scheduleJob: Job? = null
    private var scheduleRequestId = 0
    private val anchorDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    val showDatePicker: StateFlow<Boolean> field = MutableStateFlow(false)
    val selectedDate: StateFlow<LocalDate> field = MutableStateFlow(anchorDate)
    private var loadedWeekStart: LocalDate? = null
    private var requestedWeekStart: LocalDate? = null

    init {
        if (activeGroupName.value != null) {
            loadSchedule(anchorDate)
        } else {
            viewModelScope.launch {
                preferencesRepository.groupNameFlow.collect { savedGroupName ->
                    val name = savedGroupName.takeIf { it.isNotBlank() } ?: return@collect
                    if (name != activeGroupName.value) {
                        activeGroupName.value = name
                        schedule.value = null
                        loadedWeekStart = null
                        requestedWeekStart = null
                        scheduleJob?.cancel()
                        loadSchedule(selectedDate.value)
                    }
                }
            }
        }
    }

    private val pagerPageCount = 600
    private val centerPage = pagerPageCount / 2

    val pagerState = PagerState(
        currentPage = centerPage,
        pageCount = { pagerPageCount }
    )

    fun openPicker() { showDatePicker.value = true }
    fun closePicker() { showDatePicker.value = false }
    fun selectEvent(event: ScheduleEvent) { selectedEvent.value = event }
    fun dismissEvent() { selectedEvent.value = null }

    fun dateForPage(page: Int): LocalDate =
        anchorDate.plus(DatePeriod(days = page - centerPage))

    fun pageForDate(date: LocalDate): Int =
        centerPage + (date.toEpochDays() - anchorDate.toEpochDays()).toInt()

    fun onPageChanged(page: Int) {
        selectDate(dateForPage(page))
    }

    fun changeDateAndClose(date: LocalDate) {
        showDatePicker.value = false
        selectDate(date)
    }

    fun retry() {
        loadSchedule(selectedDate.value)
    }

    private fun selectDate(date: LocalDate) {
        selectedDate.value = date
        loadSchedule(date)
    }

    private fun loadSchedule(date: LocalDate) {
        val selectedGroup = activeGroupName.value ?: return
        val daysSinceMonday = (date.toEpochDays() + 3).mod(7)
        val weekStart = date.minus(DatePeriod(days = daysSinceMonday))
        if (weekStart == loadedWeekStart || weekStart == requestedWeekStart) return

        val requestId = ++scheduleRequestId
        requestedWeekStart = weekStart
        scheduleJob?.cancel()
        scheduleJob = viewModelScope.launch {
            isLoading.value = true
            error.value = null
            try {
                val result = repository.getSchedule(selectedGroup, date)
                if (requestId == scheduleRequestId) {
                    schedule.value = result
                    loadedWeekStart = weekStart
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (requestId == scheduleRequestId) {
                    error.value = e.message ?: "Не удалось загрузить расписание"
                }
            } finally {
                if (requestId == scheduleRequestId) {
                    requestedWeekStart = null
                    isLoading.value = false
                }
            }
        }
    }
}