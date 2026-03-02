package com.example.sbtv.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.sbtv.data.model.IPTVProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.preferencesDataStore

val Context.providerDataStore by preferencesDataStore(name = "iptv_providers")

class ProviderDataStore(private val context: Context) {

    private val gson = Gson()

    private val PROVIDERS_KEY = stringPreferencesKey("providers")

    suspend fun saveProviders(providers: List<IPTVProvider>) {
        val json = gson.toJson(providers)
        context.providerDataStore.edit {
            it[PROVIDERS_KEY] = json
        }
    }

    fun getProviders(): Flow<List<IPTVProvider>> {
        return context.providerDataStore.data.map { prefs ->
            val json = prefs[PROVIDERS_KEY] ?: return@map emptyList()

            val type = object : TypeToken<List<IPTVProvider>>() {}.type
            gson.fromJson(json, type)
        }
    }
}