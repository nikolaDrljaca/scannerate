package com.drbrosdev.studytextscan.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    val isFirstLaunch: Flow<Boolean>
        get() = dataStore.data.map {
            it[FIRST_LAUNCH] ?: false
        }

    suspend fun firstLaunchComplete() {
        dataStore.edit {
            it[FIRST_LAUNCH] = true
        }
    }

    private companion object {
        val FIRST_LAUNCH = booleanPreferencesKey(name = "first_launch")
    }
}