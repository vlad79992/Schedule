package com.moshk.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moshk.schedule.data.DaySchedule
import com.moshk.schedule.navigation.LocalNavBackStack
import com.moshk.schedule.navigation.Route
import com.moshk.schedule.ui.bottomBar.BottomBar
import com.moshk.schedule.ui.bottomBar.DatePickerModal
import com.moshk.schedule.ui.schedule.DayLayout
import com.moshk.schedule.ui.screens.viewmodels.ScheduleScreenViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ScheduleScreen(groupName: String?) {
    val viewmodel = koinViewModel<ScheduleScreenViewModel>(
        parameters = { parametersOf("15.27д-ивт01/24б") }
        // parameters = { parametersOf(groupName) }
    )
    val backstack = LocalNavBackStack.current


    val bottomSpacerHeight = 120.dp
    val selectedDate by viewmodel.selectedDate.collectAsStateWithLifecycle()
    val schedule by viewmodel.schedule.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            BottomBar(
                pagerState = viewmodel.pagerState,
                selectedDate = viewmodel.selectedDate.value,
                paddingValues = PaddingValues(5.dp, 20.dp),
                onDateClick = { viewmodel.openPicker() },
                onSettingsClick = { backstack.add(Route.Settings) },
                onSearchClick = { backstack.add(Route.Search) },
                height = bottomSpacerHeight
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(layoutDirection = LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(layoutDirection = LocalLayoutDirection.current)
                )
        ) {
            when {
                viewmodel.groupName.isNullOrEmpty() -> EmptyScreen("Выберите группу")

                schedule == null -> EmptyScreen("Загрузка расписания...")

                else -> {
                    val daySchedule = schedule!![selectedDate]

                    if (daySchedule != null) {
                        DayLayout(
                            daySchedule = daySchedule,
                            onEventClick = { event -> /* TODO */ },
                            bottomSpacerHeight = bottomSpacerHeight
                        )
                    } else {
                        EmptyScreen("Нет занятий на $selectedDate")
                    }
                }
            }
        }
    }

    if (viewmodel.showDatePicker.collectAsStateWithLifecycle().value) {
        DatePickerModal(
            onDateSelected = { millis ->
                millis?.let { timestamp ->
                    val instant = kotlin.time.Instant.fromEpochMilliseconds(timestamp)
                    val selected = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    viewmodel.changeDateAndClose(selected)
                }
            },
            onDismiss = { viewmodel.closePicker() },
            initialDate = selectedDate
        )
    }
}

@Composable
private fun EmptyScreen(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}