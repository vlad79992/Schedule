package com.moshk.schedule.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moshk.schedule.navigation.LocalNavBackStack
import com.moshk.schedule.navigation.Route
import com.moshk.schedule.ui.components.GroupPicker
import com.moshk.schedule.ui.screens.viewmodels.SettingsScreenViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@PreviewLightDark
@Composable
fun SettingsScreen() {
    val viewmodel = koinViewModel<SettingsScreenViewModel>()
    val backstack = LocalNavBackStack.current
    val coroutineScope = rememberCoroutineScope()
    val groupName by viewmodel.groupName.collectAsStateWithLifecycle()
    val groups by viewmodel.groups.collectAsStateWithLifecycle()
    val isLoading by viewmodel.isLoadingGroups.collectAsStateWithLifecycle()
    val groupsError by viewmodel.groupsError.collectAsStateWithLifecycle()
    val saveError by viewmodel.saveError.collectAsStateWithLifecycle()

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выберите группу",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            if (saveError != null) {
                Text(
                    text = saveError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            GroupPicker(
                groups = groups,
                isLoading = isLoading,
                error = groupsError,
                selectedGroupName = groupName,
                onRetry = viewmodel::loadGroups,
                onGroupSelected = { group ->
                    coroutineScope.launch {
                        if (viewmodel.selectGroup(group.name)) {
                            backstack.add(Route.Schedule(group.name))
                        }
                    }
                }
            )
        }
    }
}