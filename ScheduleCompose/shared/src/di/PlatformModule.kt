package com.moshk.schedule.di

import androidx.compose.runtime.Composable
import org.koin.core.module.Module

@Composable
expect fun rememberPlatformModules(): List<Module>
