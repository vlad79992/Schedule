package com.moshk.schedule.ui.schedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.moshk.schedule.data.ScheduleEvent
import com.moshk.schedule.ui.theme.ScheduleAppTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
fun EventCard(
    event : ScheduleEvent,
    shape: RoundedCornerShape,
    onClick: () -> Unit
) {
    val timeFormat = LocalDateTime.Format {
        byUnicodePattern("HH:mm")
    }

    val startTime = event.start.format(timeFormat)
    val endTime = event.end.format(timeFormat)

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        shape = shape
    ) {
        Column(modifier = Modifier
            .padding(12.dp)
        ) {
            Text(
                text = "$startTime – $endTime",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (event.teacher?.isNotBlank() ?: false) {
                Text(
                    text = event.teacher,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                if (event.location.isNotBlank()) {
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (event.description.isNotBlank()) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun PreviewEventCard() {
    ScheduleAppTheme(dynamicColor = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            val outerRounding = 25.dp
            val innerRounding = 5.dp
            EventCard(
                event = ScheduleEvent(
                    name = "Системы формирования и исполнения решений",
                    teacher = "Афанасьев М.А.",
                    location = "3 корпус - 214",
                    start = LocalDateTime(2026, 2, 28, 10, 10),
                    end = LocalDateTime(2026, 2, 28, 11, 40),
                    description = "Лекция",
                    id ="0"
                ),
                shape = RoundedCornerShape(
                    outerRounding,
                    outerRounding,
                    innerRounding,
                    innerRounding
                ),
                onClick = {}
            )
        }
    }
}