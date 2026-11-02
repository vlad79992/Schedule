package com.moshk.schedule.data

import kotlinx.datetime.LocalDate

data class DaySchedule(
    val date: LocalDate,
    val events: List<ScheduleEvent>
)