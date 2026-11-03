package com.moshk.schedule.data

actual fun resolveScheduleApiBaseUrl(baseUrl: String): String =
    baseUrl.replace("://localhost", "://10.0.2.2")
