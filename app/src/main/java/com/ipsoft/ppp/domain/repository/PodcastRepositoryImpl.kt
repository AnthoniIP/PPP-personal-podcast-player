package com.ipsoft.ppp.domain.repository

import com.ipsoft.ppp.data.datastore.PodcastDataStore
import com.ipsoft.ppp.data.network.service.PodcastService
import com.ipsoft.ppp.domain.model.PodcastSearch
import com.ipsoft.ppp.error.Failure
import com.ipsoft.ppp.util.Either
import com.ipsoft.ppp.util.left
import com.ipsoft.ppp.util.right

class PodcastRepositoryImpl(
    private val service: PodcastService,
    private val dataStore: PodcastDataStore
) : PodcastRepository {

    companion object {
        private const val TAG = "PodcastRepository"
    }

    override suspend fun searchPodcasts(
        query: String,
        type: String
    ): Either<Failure, PodcastSearch> {
        return try {
            val canFetchAPI = dataStore.canFetchAPI()
            if (canFetchAPI) {
                val result = service.searchPodcasts(query, type).asDomainModel()
                dataStore.storePodcastSearchResult(result)
                right(result)
            } else {
                right(dataStore.readLastPodcastSearchResult())
            }
        } catch (e: Exception) {
            left(Failure.UnexpectedFailure)
        }
    }
}