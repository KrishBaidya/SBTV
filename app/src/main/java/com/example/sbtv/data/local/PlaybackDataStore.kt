package com.example.sbtv.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaybackDataStore(private val context: Context) {

    private val LAST_CHANNEL_KEY = stringPreferencesKey("last_channel_url")

    suspend fun saveLastChannel(url: String) {
        context.providerDataStore.edit {
            it[LAST_CHANNEL_KEY] = url
        }
    }

    fun getLastChannel(): Flow<String?> {
        return context.providerDataStore.data.map {
            it[LAST_CHANNEL_KEY]
        }
    }
}