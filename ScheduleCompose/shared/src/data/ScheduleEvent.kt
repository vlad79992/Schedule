package com.moshk.schedule.data

import kotlinx.datetime.LocalDateTime

data class ScheduleEvent(
    val id: String,
    val name: String,
    val location: String,
    val description: String,
    val teacher: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
)