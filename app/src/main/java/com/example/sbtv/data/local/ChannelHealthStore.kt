package com.example.sbtv.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.healthDataStore: DataStore<Preferences> by preferencesDataStore(name = "channel_health_prefs")

class ChannelHealthStore(private val context: Context) {
    private val gson = Gson()
    private val FAILED_STREAMS_KEY = stringPreferencesKey("failed_streams")

    suspend fun addFailedStream(url: String) {
        context.healthDataStore.edit { prefs ->
            val json = prefs[FAILED_STREAMS_KEY] ?: "[]"
            val type = object : TypeToken<MutableSet<String>>() {}.type
            val failedStreams: MutableSet<String> = gson.fromJson(json, type) ?: mutableSetOf()
            
            failedStreams.add(url)
            prefs[FAILED_STREAMS_KEY] = gson.toJson(failedStreams)
        }
    }

    fun getFailedStreams(): Flow<Set<String>> {
        return context.healthDataStore.data.map { prefs ->
            val json = prefs[FAILED_STREAMS_KEY] ?: "[]"
            val type = object : TypeToken<Set<String>>() {}.type
            gson.fromJson(json, type) ?: emptySet()
        }
    }
}
