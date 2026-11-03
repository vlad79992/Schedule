package com.moshk.schedule.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moshk.schedule.data.ScheduleGroup

@Composable
fun GroupPicker(
    groups: List<ScheduleGroup>,
    isLoading: Boolean,
    error: String?,
    selectedGroupName: String?,
    onRetry: () -> Unit,
    onGroupSelected: (ScheduleGroup) -> Unit
) {
    val searchState = rememberTextFieldState()
    val query = searchState.text.toString()
    val filteredGroups = groups.filter { it.name.contains(query, ignoreCase = true) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            state = searchState,
            label = { Text("Поиск группы") },
            modifier = Modifier.fillMaxWidth()
        )
        when {
            isLoading -> CircularProgressIndicator()
            error != null -> {
                Text(error, color = MaterialTheme.colorScheme.error)
                androidx.compose.material3.Button(onClick = onRetry) {
                    Text("Повторить")
                }
            }
            filteredGroups.isEmpty() -> Text("Группы не найдены")
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filteredGroups, key = { it.guid }) { group ->
                    Text(
                        text = group.name,
                        color = if (group.name == selectedGroupName) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGroupSelected(group) }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}
