package com.ipsoft.ppp.domain.repository

import com.ipsoft.ppp.domain.model.PodcastSearch
import com.ipsoft.ppp.error.Failure
import com.ipsoft.ppp.util.Either

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
    ): Either<Failure, PodcastSearch>
}