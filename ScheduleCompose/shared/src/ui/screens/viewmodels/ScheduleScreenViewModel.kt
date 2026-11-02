package com.moshk.schedule.ui.screens.viewmodels

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moshk.schedule.data.DaySchedule
import com.moshk.schedule.data.ScheduleEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds

class ScheduleScreenViewModel(
    val groupName: String?
) : ViewModel() {
    val schedule: StateFlow<Map<LocalDate, DaySchedule>?> field = MutableStateFlow(null)
    private val currentTimeZone = TimeZone.currentSystemDefault()

    private val currentTime = MutableStateFlow(
        Clock.System.now().toLocalDateTime(currentTimeZone)
    )

    init {
        viewModelScope.launch {
            while (true) {
                delay(1.seconds)
                currentTime.value = Clock.System.now().toLocalDateTime(currentTimeZone)
            }
        }

        viewModelScope.launch {
            try {
                // schedule.value = getCheduleFromRaspRea(groupName)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }

    private val pagerPageCount = 600
    private val centerPage = pagerPageCount / 2

    val pagerState = PagerState(
        currentPage = centerPage,
        pageCount = { pagerPageCount }
    )

    var selectedEvent: ScheduleEvent? = null
    val showDatePicker: StateFlow<Boolean> field = MutableStateFlow(false)
    val selectedDate: StateFlow<LocalDate> field = MutableStateFlow(currentTime.value.date)
    fun openPicker() { showDatePicker.value = true }
    fun closePicker() { showDatePicker.value = false }
    fun changeDateAndClose(date: LocalDate) {
        selectedDate.value = date
        showDatePicker.value = false
    }
}