package com.example.sbtv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sbtv.data.local.ProviderDataStore
import kotlinx.coroutines.flow.map

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val providerDataStore = ProviderDataStore(application)

    val hasProviders = providerDataStore.getProviders().map { it.isNotEmpty() }
}
