package com.moshk.schedule.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val GROUP_NAME = stringPreferencesKey("group_name")
    }

    val groupNameFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GROUP_NAME] ?: ""
    }

    suspend fun saveGroupName(groupName: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.GROUP_NAME] = groupName
        }
    }

}
