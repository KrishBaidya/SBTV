package com.example.sbtv.data.local

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.data.model.IPTVProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProviderViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = ProviderDataStore(application)

    val providers = dataStore.getProviders()

    fun addProvider(provider: IPTVProvider) {
        viewModelScope.launch {
            val current = providers.first()
            dataStore.saveProviders(current + provider)
        }
    }
}