package com.moshk.schedule.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.moshk.schedule.createDataStore
import org.koin.core.module.Module
import org.koin.dsl.module

@Composable
actual fun rememberPlatformModules(): List<Module> {
    val context = LocalContext.current.applicationContext
    return remember(context) {
        listOf(
            module {
                single<DataStore<Preferences>> {
                    createDataStore(context = context)
                }
            }
        )
    }
}