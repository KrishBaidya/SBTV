package com.example.sbtv.data.local

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.data.model.ProviderType
import com.example.sbtv.data.repository.IPTVRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProviderViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ProviderDataStore(application)
    private val repository = IPTVRepository()

    val providers = dataStore.getProviders()
    val activeProviderId = dataStore.getActiveProviderId()

    suspend fun testConnection(provider: IPTVProvider): Boolean {
        return try {
            Log.d("ProviderViewModel", "Testing connection to: ${provider.baseUrl}")
            when (provider.type) {
                ProviderType.M3U -> {
                    val parsed = repository.loadM3UChannels(provider.baseUrl)
                    val hasContent = parsed.channels.isNotEmpty() || parsed.movies.isNotEmpty() || parsed.series.isNotEmpty()
                    Log.d("ProviderViewModel", "M3U Test result: $hasContent (C:${parsed.channels.size}, M:${parsed.movies.size}, S:${parsed.series.size})")
                    hasContent
                }
                ProviderType.XTREAM -> {
                    val channels = repository.loadXtreamChannels(provider.baseUrl, provider.username ?: "", provider.password ?: "")
                    Log.d("ProviderViewModel", "Xtream Test result: ${channels.isNotEmpty()}")
                    channels.isNotEmpty()
                }
            }
        } catch (e: Exception) {
            Log.e("ProviderViewModel", "Connection test failed", e)
            false
        }
    }

    fun addProvider(provider: IPTVProvider) {
        viewModelScope.launch {
            val current = providers.first()
            dataStore.saveProviders(current + provider)
            // Make new provider the active one instantly
            dataStore.setActiveProviderId(provider.id)
            Log.d("ProviderViewModel", "Provider added: ${provider.name}")
        }
    }

    fun deleteProvider(id: String) {
        viewModelScope.launch {
            val current = providers.first()
            val updated = current.filter { it.id != id }
            dataStore.saveProviders(updated)
            if (activeProviderId.first() == id) {
                dataStore.setActiveProviderId(updated.firstOrNull()?.id)
            }
            Log.d("ProviderViewModel", "Provider deleted: $id")
        }
    }

    fun renameProvider(id: String, newName: String) {
        viewModelScope.launch {
            val current = providers.first()
            val updated = current.map { if (it.id == id) it.copy(name = newName) else it }
            dataStore.saveProviders(updated)
            Log.d("ProviderViewModel", "Provider renamed: $id to $newName")
        }
    }

    fun setActiveProvider(id: String) {
        viewModelScope.launch {
            dataStore.setActiveProviderId(id)
            Log.d("ProviderViewModel", "Active provider set to: $id")
        }
    }
}