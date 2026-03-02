package com.example.sbtv.ui.screens.tv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.data.local.ProviderDataStore
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.model.ProviderType
import com.example.sbtv.data.repository.IPTVRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MoviesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = IPTVRepository()
    private val providerStore = ProviderDataStore(application)

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            
            providerStore.getProviders().collectLatest { providers ->
                val provider = providers.firstOrNull()
                if (provider != null) {
                    try {
                        if (provider.type == ProviderType.XTREAM) {
                            val result = repository.loadXtreamMovies(
                                baseUrl = provider.baseUrl,
                                username = provider.username ?: "",
                                password = provider.password ?: ""
                            )
                            _movies.value = result
                        } else {
                            // Currently we don't handle M3U VOD parsing separately, 
                            // it would usually be mixed in channels or require specialized parsing.
                            _movies.value = emptyList()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _movies.value = emptyList()
                    } finally {
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                    _movies.value = emptyList()
                }
            }
        }
    }
}
