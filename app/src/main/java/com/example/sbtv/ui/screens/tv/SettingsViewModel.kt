package com.example.sbtv.ui.screens.tv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.data.local.ProviderDataStore
import com.example.sbtv.data.model.IPTVProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ProviderDataStore(application)

    val allProviders: StateFlow<List<IPTVProvider>> = dataStore.getProviders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeProviderId = MutableStateFlow<String?>(null)
    val activeProviderId: StateFlow<String?> = _activeProviderId

    fun setActiveProvider(id: String) {
        viewModelScope.launch {
            val providers = allProviders.value
            val provider = providers.find { it.id == id }
            if (provider != null) {
                // Move selected provider to the top so it becomes active in TVViewModel
                val updatedProviders = providers.toMutableList()
                updatedProviders.remove(provider)
                updatedProviders.add(0, provider)
                dataStore.saveProviders(updatedProviders)
                _activeProviderId.value = id
            }
        }
    }

    fun deleteProvider(id: String) {
        viewModelScope.launch {
            val providers = allProviders.value
            val updatedProviders = providers.filter { it.id != id }
            dataStore.saveProviders(updatedProviders)
            if (_activeProviderId.value == id) {
                _activeProviderId.value = null
            }
        }
    }

    fun clearAll(onCleared: () -> Unit) {
        viewModelScope.launch {
            dataStore.saveProviders(emptyList())
            _activeProviderId.value = null
            onCleared()
        }
    }
}
