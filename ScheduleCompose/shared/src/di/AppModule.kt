package com.moshk.schedule.di

import com.moshk.schedule.data.UserPreferencesRepository
import com.moshk.schedule.navigation.Route
import com.moshk.schedule.ui.screens.ScheduleScreen
import com.moshk.schedule.ui.screens.SearchScreen
import com.moshk.schedule.ui.screens.SettingsScreen
import com.moshk.schedule.ui.screens.viewmodels.ScheduleScreenViewModel
import com.moshk.schedule.ui.screens.viewmodels.SearchScreenViewModel
import com.moshk.schedule.ui.screens.viewmodels.SettingsScreenViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val appModule = module {
    single { UserPreferencesRepository(dataStore = get()) }
    viewModel { (groupName: String?) -> ScheduleScreenViewModel(groupName) }
    viewModel { SearchScreenViewModel(repository = get()) }
    viewModel { SettingsScreenViewModel(repository = get()) }

    navigation<Route.Search> {
        SearchScreen()
    }

    navigation<Route.Settings> {
        SettingsScreen()
    }

    navigation<Route.Schedule> { route ->
        ScheduleScreen(groupName = route.groupName)
    }
}