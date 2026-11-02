package com.moshk.schedule.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val GROUP_NAME = stringPreferencesKey("group_name")
        val SCHEDULE_PROVIDER = stringPreferencesKey("schedule_provider")
    }

    val groupNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROUP_NAME] ?: ""
    }

    val scheduleProviderFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SCHEDULE_PROVIDER] ?: ""
    }

    suspend fun saveGroupName(groupName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROUP_NAME] = groupName
        }
    }

    suspend fun setScheduleProvider(provider: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SCHEDULE_PROVIDER] = provider
        }
    }
}
