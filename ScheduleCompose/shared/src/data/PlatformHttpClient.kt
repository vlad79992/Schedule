package com.moshk.schedule.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

const val SCHEDULE_API_BASE_URL = "http://localhost:5287"

expect fun createPlatformHttpClient(): HttpClient

expect fun resolveScheduleApiBaseUrl(baseUrl: String): String

fun HttpClientConfig<*>.configureScheduleHttpClient() {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

fun createScheduleHttpClient(): HttpClient = createPlatformHttpClient()
