package com.moshk.schedule.ui.bottomBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.moshk.schedule.resources.Res
import com.moshk.schedule.resources.search_48px
import com.moshk.schedule.resources.settings_48px
import com.moshk.schedule.ui.theme.ScheduleAppTheme
import kotlinx.datetime.*
import kotlinx.datetime.format.*
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

@Composable
fun BottomBar(
    pagerState: PagerState,
    selectedDate: LocalDate,
    paddingValues: PaddingValues,
    onDateClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    height: Dp? = null,
) {
    val outerRounding = 20.dp
    val innerRounding = 5.dp
    Row(
        if (height == null)
        {
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(paddingValues)
        } else {
            Modifier
                .fillMaxWidth()
                .height(height)
                .padding(paddingValues)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val buttonColors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface,
        )
        // КНОПКА НАСТРОЕК
        ElevatedButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            colors = buttonColors,
            shape = RoundedCornerShape(
                topStart = outerRounding,
                topEnd = innerRounding,
                bottomEnd = innerRounding,
                bottomStart = outerRounding
            )
        ) {
            Icon(
                painterResource(Res.drawable.settings_48px),
                contentDescription = "Настройки",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxSize()
            )
        }

        // КНОПКПА ДАТЫ
        ElevatedButton(
            onClick = onDateClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = buttonColors,
            shape = RoundedCornerShape(
                topStart = innerRounding,
                topEnd = innerRounding,
                bottomEnd = innerRounding,
                bottomStart = innerRounding
            )
        ) {
            if (pagerState.isScrollInProgress) {
                AnimatedDate(pagerState, selectedDate)
            } else {
                DateContent(selectedDate)
            }
        }

        // КНОПКА ПОИСКА
        ElevatedButton(
            onClick = onSearchClick,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            colors = buttonColors,
            shape = RoundedCornerShape(
                topStart = innerRounding,
                topEnd = outerRounding,
                bottomEnd = outerRounding,
                bottomStart = innerRounding
            )
        ) {
            Icon(
                painterResource(Res.drawable.search_48px),
                contentDescription = "Поиск",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


private const val PROGRESS_MULT = 2f
@Composable
fun AnimatedDate(pagerState: PagerState,
                 localDate: LocalDate,
                 modifier: Modifier = Modifier
) {
    val animationParams by remember(pagerState) {
        derivedStateOf {
            val offset = pagerState.currentPageOffsetFraction
            val progress = abs(offset) * PROGRESS_MULT
            val nextDate =
                if (offset >= 0) localDate.plus(DatePeriod(days = 1))
                else localDate.minus(DatePeriod(days = 1))
            Triple(progress, nextDate, offset)
        }
    }

    val (progress, nextDate, offset) = animationParams

    Box {
        key(localDate) {
            DateContent(
                date = localDate,
                modifier = modifier
                    .graphicsLayer(alpha = 1f - progress)
                    .offset((-25 * offset).dp, 0.dp)
            )
        }
        key(nextDate){
            DateContent(
                date = nextDate,
                modifier = modifier
                    .graphicsLayer(alpha = progress - PROGRESS_MULT + 1f)
                    .offset((-25 * offset).dp, 0.dp)
            )
        }
    }
}

@OptIn(FormatStringsInDatetimeFormats::class)
@Composable
private fun DateContent(date: LocalDate, modifier: Modifier = Modifier) {
    val dayName = remember(date) {
        date.format(
            LocalDate.Format {
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
        )
    }
    val dateString = remember(date) {
        date.format(
            LocalDate.Format {
                byUnicodePattern("dd.MM.yyyy")
            }
        )
    }

    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = dateString,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@PreviewLightDark
@Composable
fun PreviewBottomBar() {
    ScheduleAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val pagerState = rememberPagerState(
                initialPage = 15,
                pageCount = { 30 }
            )
            BottomBar(
                pagerState = pagerState,
                selectedDate = LocalDate(2026, 3, 16),
                paddingValues = PaddingValues(10.dp)
            )
        }
    }
}