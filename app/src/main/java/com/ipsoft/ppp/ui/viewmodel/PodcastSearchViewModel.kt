package com.ipsoft.ppp.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipsoft.ppp.domain.model.Episode
import com.ipsoft.ppp.domain.model.PodcastSearch
import com.ipsoft.ppp.domain.repository.PodcastRepository
import com.ipsoft.ppp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastSearchViewModel @Inject constructor(
    private val repository: PodcastRepository,
) : ViewModel() {

    var podcastSearch by mutableStateOf<Resource<PodcastSearch>>(Resource.Loading)
        private set

    init {
        searchPodcasts()
    }

    fun getPodcastDetail(id: String): Episode? {
        return when (podcastSearch) {
            is Resource.Error -> null
            Resource.Loading -> null
            is Resource.Success -> (
                podcastSearch as Resource.Success<PodcastSearch>
                ).data.results.find { it.id == id }
        }
    }

    fun searchPodcasts(query: String = "fiction") {
        viewModelScope.launch {
            podcastSearch = Resource.Loading
            val result = repository.searchPodcasts(query, "episode")
            result.fold(
                { failure ->
                    podcastSearch = Resource.Error(failure)
                },
                { data ->
                    podcastSearch = Resource.Success(data)
                },
            )
        }
    }
}
