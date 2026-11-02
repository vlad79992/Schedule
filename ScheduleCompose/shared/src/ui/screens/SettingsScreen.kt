package com.moshk.schedule.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moshk.schedule.navigation.LocalNavBackStack
import com.moshk.schedule.ui.screens.viewmodels.SettingsScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@PreviewLightDark
@Composable
fun SettingsScreen() {
    val viewmodel = koinViewModel<SettingsScreenViewModel>()
    val backstack = LocalNavBackStack.current

    val groupName by viewmodel.groupName.collectAsStateWithLifecycle()
    val groupNameState = rememberTextFieldState()

    LaunchedEffect(groupName) {
        if (groupNameState.text.toString() != groupName) {
            groupNameState.edit {
                replace(0, length, groupName)
            }
        }
    }

    val provider by viewmodel.scheduleProvider.collectAsStateWithLifecycle()
    var expanded by remember { mutableStateOf(false) }
    var selectedProvider by remember { mutableStateOf("Выберите провайдера...") }
    LaunchedEffect(provider) {
        if (provider.isNotEmpty()) {
            selectedProvider = provider
        }
    }

    Scaffold {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                state = groupNameState,
                label = { Text("Группа") },
                isError = groupNameState.text.isBlank(),
                supportingText = {
                    if (groupNameState.text.isBlank()) {
                        Text("Введите группу")
                    }
                }
            )
            Spacer(Modifier.height(24.dp))
            Text("Провайдер расписания")
            Box(modifier = Modifier.wrapContentSize()) {
                val topCornerRadius by animateDpAsState(
                    targetValue = if (expanded) 16.dp else 40.dp,
                    animationSpec = tween(durationMillis = 150)
                )
                val bottomCornerRadius by animateDpAsState(
                    targetValue = if (expanded) 8.dp else 40.dp,
                    animationSpec = tween(durationMillis = 150)
                )

                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.width(300.dp),
                    shape = RoundedCornerShape(
                        topStart = topCornerRadius,
                        topEnd = topCornerRadius,
                        bottomStart = bottomCornerRadius,
                        bottomEnd = bottomCornerRadius
                    )
                ) {
                    Text(selectedProvider)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(300.dp),
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp,
                        bottomStart = 16.dp,
                        bottomEnd = 16.dp
                    )
                ) {
                    listOf("rasp.rea.ru", "pleh.tech").forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedProvider = item
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewmodel.updateGroupName(groupNameState.text.toString())
                    viewmodel.updateProvider(selectedProvider)
                },
                modifier = Modifier.width(200.dp)
            ) {
                Text("Сохранить")
            }
        }
    }
}