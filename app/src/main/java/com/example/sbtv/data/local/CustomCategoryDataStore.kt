package com.example.sbtv.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.sbtv.data.model.CustomCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.customCategoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "custom_categories_prefs")

class CustomCategoryDataStore(private val context: Context) {
    private val gson = Gson()
    private val CATEGORIES_KEY = stringPreferencesKey("custom_categories")

    suspend fun saveCategories(categories: List<CustomCategory>) {
        val json = gson.toJson(categories)
        context.customCategoryDataStore.edit { prefs ->
            prefs[CATEGORIES_KEY] = json
        }
    }

    fun getCategories(): Flow<List<CustomCategory>> {
        return context.customCategoryDataStore.data.map { prefs ->
            val json = prefs[CATEGORIES_KEY] ?: return@map emptyList()
            val type = object : TypeToken<List<CustomCategory>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }
}
