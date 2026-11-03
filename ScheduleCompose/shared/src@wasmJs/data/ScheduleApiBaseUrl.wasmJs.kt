package com.moshk.schedule.data

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@JsFun("() => globalThis.location.origin")
private external fun currentPageOrigin(): String

actual fun resolveScheduleApiBaseUrl(baseUrl: String): String = currentPageOrigin()
