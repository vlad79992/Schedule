package com.moshk.schedule.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.moshk.schedule.data.ScheduleEvent
import com.moshk.schedule.resources.Res
import com.moshk.schedule.resources.trending_flat_48px
import com.moshk.schedule.ui.theme.ScheduleAppTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun EventScreen(event: ScheduleEvent) {
    val timeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm")
    }
    val dayNameFormat = LocalDateTime.Format {
        dayOfWeek(
            DayOfWeekNames(
                "Понедельник",
                "Вторник",
                "Среда",
                "Четверг",
                "Пятница",
                "Суббота",
                "Воскресенье"
            )
        )
    }
    val dateFormat = LocalDateTime.Format {
        byUnicodePattern("dd.MM.yyyy")
    }
    val startTime = event.start?.format(timeFormat) ?: "??:??"
    val endTime = event.end?.format(timeFormat) ?: "??:??"
    val now = Clock.System.now().toLocalDateTime(currentSystemDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Row {
            Text(
                text = startTime,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                painterResource(Res.drawable.trending_flat_48px),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(60.dp)
                    .align(alignment = Alignment.CenterVertically)
            )
            Text(
                text = endTime,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
        LinearWavyProgressIndicator(
            progress = {
                if (now < event.start!!) {
                    0f
                } else if (now < event.end!!) {
                    (now.toInstant(UTC) - event.start.toInstant(UTC))
                        .div(event.end.toInstant(UTC) - event.start.toInstant(UTC))
                        .toFloat()
                } else {
                    1f
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )
        Text(
            text = event.start!!.format(dayNameFormat).uppercase(),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 0.dp)
        )
        Text(
            text = event.start.format(dateFormat),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 0.dp, bottom = 12.dp)
        )

        Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        Text(
            text = event.location,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
        )

        Text(
            text = event.description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )

        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 0.dp,
                    vertical = 12.dp
                )
        ) {
            Text(
                text = if (now < event.start) {
                    "Начнётся через ${
                        timeText(
                            event.start.toInstant(UTC)
                                .minus(now.toInstant(UTC))
                        )
                    }"
                } else if (now < event.end!!) {
                    "Закончится через ${
                        timeText(
                            event.end.toInstant(UTC)
                                .minus(now.toInstant(UTC))
                        )
                    }"
                } else {
                    "Завершено"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun timeText(toEvent: Duration): String {
    val days = toEvent.inWholeDays
    val hours = toEvent.inWholeHours % 24
    val minutes = toEvent.inWholeMinutes % 60

    return buildString {
        if (days > 0) append("$days д ")
        if (hours > 0 || days > 0) append("$hours ч ")
        append("$minutes мин")
    }
}

@PreviewLightDark
@Composable
fun PreviewEventScreen() {
    ScheduleAppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            val now = Clock.System.now()
            EventScreen(
                event = ScheduleEvent(
                    name = "Системы формирования и исполнения решений",
                    location = "3 корпус - 214",
                    start = now.minus(20.minutes).toLocalDateTime(currentSystemDefault()),
                    end = now.plus(30.minutes).toLocalDateTime(currentSystemDefault()),
                    description = "Лекция",
                    id = "0",
                    teacher = null,
                )
            )
        }
    }
}