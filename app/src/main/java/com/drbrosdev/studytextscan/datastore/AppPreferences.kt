package com.drbrosdev.studytextscan.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    val showReward: Flow<Boolean>
        get() = dataStore.data.map {
            it[SHOW_REWARD] ?: false
        }

    val scanCount: Flow<Int>
        get() = dataStore.data.map {
            it[SCAN_COUNT] ?: 6
        }

    val isFirstLaunch: Flow<Boolean>
        get() = dataStore.data.map {
            it[FIRST_LAUNCH] ?: false
        }

    suspend fun firstLaunchComplete() {
        dataStore.edit {
            it[FIRST_LAUNCH] = true
        }
    }

    suspend fun showReward() {
        dataStore.edit {
            it[SHOW_REWARD] = true
        }
    }

    suspend fun rewardShown() {
        dataStore.edit {
            it[SHOW_REWARD] = false
        }
    }

    suspend fun incrementSupportCount() {
        dataStore.edit {
            var current = it[SCAN_COUNT] ?: 6
            current += 1
            it[SCAN_COUNT] = current
        }
    }

    private companion object {
        val FIRST_LAUNCH = booleanPreferencesKey(name = "first_launch")
        val SHOW_REWARD = booleanPreferencesKey(name = "show_reward")
        val SCAN_COUNT = intPreferencesKey(name = "scan_count")
    }
}