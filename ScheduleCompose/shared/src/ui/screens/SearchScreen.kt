package com.moshk.schedule.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moshk.schedule.ui.components.GroupPicker
import com.moshk.schedule.navigation.LocalNavBackStack
import com.moshk.schedule.navigation.Route
import com.moshk.schedule.ui.screens.viewmodels.SearchScreenViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen() {
    val viewModel = koinViewModel<SearchScreenViewModel>()
    val backstack = LocalNavBackStack.current
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (error != null && groups.isNotEmpty()) {
                Text(error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            GroupPicker(
                groups = groups,
                isLoading = isLoading,
                error = if (groups.isEmpty()) error else null,
                selectedGroupName = null,
                onRetry = viewModel::loadGroups,
                onGroupSelected = { group ->
                    coroutineScope.launch {
                        if (viewModel.selectGroup(group.name)) {
                            backstack.add(Route.Schedule(group.name))
                        }
                    }
                }
            )
        }
    }
}