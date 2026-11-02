package com.moshk.schedule

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.moshk.schedule.navigation.NavigationRoot
import com.moshk.schedule.di.appModule
import com.moshk.schedule.di.rememberPlatformModules
import com.moshk.schedule.ui.theme.ScheduleAppTheme
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    val platformModules = rememberPlatformModules()
    KoinApplication(configuration = koinConfiguration(declaration = {
        modules(platformModules)
        modules(appModule)
    }), content = {
        ScheduleAppTheme {
            NavigationRoot()
        }
    })
}
