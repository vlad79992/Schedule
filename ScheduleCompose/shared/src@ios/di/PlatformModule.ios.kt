package com.moshk.schedule.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.moshk.schedule.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

@Composable
actual fun rememberPlatformModules(): List<Module> {
    return remember {
        listOf(
            module {
                single<DataStore<Preferences>> {
                    createDataStore()
                }
            }
        )
    }
}