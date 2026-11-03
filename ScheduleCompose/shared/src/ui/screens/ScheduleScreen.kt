package com.moshk.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moshk.schedule.navigation.LocalNavBackStack
import com.moshk.schedule.navigation.Route
import com.moshk.schedule.ui.bottomBar.BottomBar
import com.moshk.schedule.ui.bottomBar.DatePickerModal
import com.moshk.schedule.ui.schedule.DayLayout
import com.moshk.schedule.ui.schedule.EventScreen
import com.moshk.schedule.ui.screens.viewmodels.ScheduleScreenViewModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(groupName: String?) {
    val viewmodel = koinViewModel<ScheduleScreenViewModel>(
        parameters = { parametersOf(groupName) }
    )
    val backstack = LocalNavBackStack.current
    val coroutineScope = rememberCoroutineScope()

    val bottomSpacerHeight = 120.dp
    val selectedDate by viewmodel.selectedDate.collectAsStateWithLifecycle()
    val activeGroupName by viewmodel.activeGroupName.collectAsStateWithLifecycle()
    val schedule by viewmodel.schedule.collectAsStateWithLifecycle()
    val isLoading by viewmodel.isLoading.collectAsStateWithLifecycle()
    val error by viewmodel.error.collectAsStateWithLifecycle()
    val selectedEvent by viewmodel.selectedEvent.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(viewmodel.pagerState) {
        snapshotFlow { viewmodel.pagerState.settledPage }
            .distinctUntilChanged()
            .collect(viewmodel::onPageChanged)
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                pagerState = viewmodel.pagerState,
                selectedDate = selectedDate,
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
            if (activeGroupName.isNullOrEmpty()) {
                EmptyScreen("Выберите группу")
            } else {
                HorizontalPager(
                    state = viewmodel.pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val pageDate = viewmodel.dateForPage(page)
                    when {
                        error != null -> EmptyScreen(error!!, "Повторить", viewmodel::retry)

                        isLoading -> EmptyScreen("Загрузка расписания...")

                        schedule == null ->
                            EmptyScreen("Расписание не загружено", "Повторить", viewmodel::retry)

                        else -> {
                            val daySchedule = schedule?.get(pageDate)
                            if (daySchedule != null) {
                                DayLayout(
                                    daySchedule = daySchedule,
                                    onEventClick = viewmodel::selectEvent,
                                    bottomSpacerHeight = bottomSpacerHeight
                                )
                            } else {
                                EmptyScreen("Нет занятий на $pageDate")
                            }
                        }
                    }
                }
            }
        }
    }

    selectedEvent?.let { event ->
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = viewmodel::dismissEvent,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null,
            scrimColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        ) {
            EventScreen(event = event)
        }
    }

    if (viewmodel.showDatePicker.collectAsStateWithLifecycle().value) {
        DatePickerModal(
            onDateSelected = { millis ->
                millis?.let { timestamp ->
                    val instant = kotlin.time.Instant.fromEpochMilliseconds(timestamp)
                    val selected = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    viewmodel.changeDateAndClose(selected)
                    coroutineScope.launch {
                        viewmodel.pagerState.animateScrollToPage(viewmodel.pageForDate(selected))
                    }
                }
            },
            onDismiss = { viewmodel.closePicker() },
            initialDate = selectedDate
        )
    }
}

@Composable
private fun EmptyScreen(text: String, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
            if (actionText != null && onAction != null) {
                androidx.compose.material3.Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}