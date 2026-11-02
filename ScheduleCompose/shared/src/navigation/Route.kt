package com.moshk.schedule.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {
    @Serializable
    data class Schedule(val groupName: String?) : Route, NavKey {
    }

    @Serializable
    data object Search : Route, NavKey
    @Serializable
    data object Settings : Route, NavKey
}