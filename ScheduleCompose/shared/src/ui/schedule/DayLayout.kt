package com.moshk.schedule.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moshk.schedule.data.DaySchedule
import com.moshk.schedule.data.ScheduleEvent
import com.moshk.schedule.ui.theme.ScheduleAppTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Composable
fun DayLayout(
    daySchedule: DaySchedule,
    onEventClick: (ScheduleEvent) -> Unit,
    bottomSpacerHeight: Dp
) {
    if (daySchedule.events.isNotEmpty()) {
        val outerRounding = 20.dp
        val innerRounding = 5.dp
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(
                items = daySchedule.events,
                key = { _, event -> "${event.start}_${event.name}" }
            ) { index, event ->

                val shape = when {
                    daySchedule.events.size == 1 -> RoundedCornerShape(outerRounding)
                    index == 0 -> RoundedCornerShape(
                        outerRounding, outerRounding, innerRounding, innerRounding
                    )
                    index == daySchedule.events.lastIndex -> RoundedCornerShape(
                        innerRounding, innerRounding, outerRounding, outerRounding
                    )
                    else -> RoundedCornerShape(innerRounding)
                }

                EventCard(event = event, shape = shape, onClick = { onEventClick(event) })
            }
            item {
                Spacer(modifier = Modifier.height(bottomSpacerHeight))
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет занятий на этот день",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewDayLayout() {
    val testDay = DaySchedule(
        date = LocalDate(2026, 3, 2),
        events = listOf(
            ScheduleEvent(
                name = "Проектирование информационной структуры потребителя информационных систем",
                location = "3 корпус - 455",
                start = LocalDateTime(2026, 3, 2, 11, 50),
                end = LocalDateTime(2026, 3, 2, 13, 20),
                description = "Практическое занятие",
                id = "0",
                teacher = null
            ),
            ScheduleEvent(
                name = "Базы данных",
                location = "3 корпус - 457",
                start = LocalDateTime(2026, 3, 2, 14, 0),
                end = LocalDateTime(2026, 3, 2, 15, 30),
                description = "Практическое занятие",
                id = "0",
                teacher = null
            ),
            ScheduleEvent(
                name = "Информационные технологии и системы в сфере цифровой экономики",
                location = "3 корпус - 455",
                start = LocalDateTime(2026, 3, 2, 15, 40),
                end = LocalDateTime(2026, 3, 2, 17, 10),
                description = "Практическое занятие",
                id = "0",
                teacher = null
            ),
            ScheduleEvent(
                name = "Управление интернет-проектом",
                location = "3 корпус - 457",
                start = LocalDateTime(2026, 3, 2, 17, 20),
                end = LocalDateTime(2026, 3, 2, 18, 50),
                description = "Практическое занятие",
                id = "0",
                teacher = null
            )
        )
    )

    ScheduleAppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DayLayout(testDay, {}, 0.dp)
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewEmptyDayLayout() {
    val testDay = DaySchedule(
        date = LocalDate(2026, 3, 2),
        events = listOf()
    )

    ScheduleAppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            DayLayout(testDay, {}, 0.dp)
        }
    }
}