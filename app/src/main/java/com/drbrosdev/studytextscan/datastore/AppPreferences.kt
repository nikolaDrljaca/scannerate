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

    val rewardCount: Flow<Int>
        get() = dataStore.data.map {
            it[REWARD_COUNT] ?: 0
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

    suspend fun incrementRewardCount() {
        dataStore.edit {
            val current = it[REWARD_COUNT] as Int
            it[REWARD_COUNT] = current + 1
        }
    }

    suspend fun incrementSupportCount() {
        dataStore.edit {
            var current = it[SCAN_COUNT] ?: 0
            current += 1
            it[SCAN_COUNT] = current
        }
    }

    private companion object {
        val FIRST_LAUNCH = booleanPreferencesKey(name = "first_launch")
        val REWARD_COUNT = intPreferencesKey(name = "reward_count")
        val SCAN_COUNT = intPreferencesKey(name = "scan_count")
    }
}