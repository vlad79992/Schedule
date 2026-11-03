package com.moshk.schedule.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleGroup(
    @SerialName("group_guid") val guid: String,
    @SerialName("group_name") val name: String,
    val faculty: String? = null,
    @SerialName("program_type") val programType: String? = null,
    val course: String? = null
)

@Serializable
private data class LessonResponse(
    val id: Int,
    val discipline: String,
    val workloadType: String? = null,
    val location: String? = null,
    val teacher: String? = null,
    val lessonStart: String? = null,
    val lessonEnd: String? = null
)

class ScheduleRepository(
    private val httpClient: HttpClient
) {
    private val baseUrl = resolveScheduleApiBaseUrl(SCHEDULE_API_BASE_URL)

    suspend fun getGroups(): List<ScheduleGroup> =
        httpClient.get(
            URLBuilder(baseUrl)
                .appendPathSegments("Schedule", "groups")
                .build()
        ).body()

    suspend fun getSchedule(groupName: String, date: LocalDate): Map<LocalDate, DaySchedule> {
        val requestUrl = URLBuilder(baseUrl)
            .appendPathSegments("Schedule", "schedule", groupName, encodeSlash = true)
            .apply { parameters.append("date", date.toString()) }
            .build()
        val lessons: List<LessonResponse> = httpClient.get(requestUrl).body()

        val events = lessons.map { lesson ->
            ScheduleEvent(
                id = lesson.id.toString(),
                name = lesson.discipline,
                location = lesson.location.orEmpty(),
                description = lesson.workloadType.orEmpty(),
                teacher = lesson.teacher,
                start = lesson.lessonStart?.toScheduleDateTime(lesson.id, "start")
                    ?: error("У занятия ${lesson.id} отсутствует время начала"),
                end = lesson.lessonEnd?.toScheduleDateTime(lesson.id, "end")
                    ?: error("У занятия ${lesson.id} отсутствует время окончания")
            )
        }

        return events.groupBy { it.start.date }
            .mapValues { (day, dayEvents) -> DaySchedule(day, dayEvents) }
    }
}

private fun String.toScheduleDateTime(lessonId: Int, fieldName: String): LocalDateTime {
    val dateTime = runCatching { LocalDateTime.parse(take(19)) }.getOrNull()
        ?: runCatching { LocalDateTime.parse(this) }.getOrNull()
        ?: error("Некорректное время $fieldName у занятия $lessonId: $this")
    return dateTime
}
