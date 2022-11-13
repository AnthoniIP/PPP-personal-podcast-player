package com.ipsoft.ppp.domain.repository

import com.ipsoft.ppp.domain.model.PodcastSearch
import com.ipsoft.ppp.error.Failure
import com.ipsoft.ppp.util.Either
import com.ipsoft.ppp.util.languageCode

interface PodcastRepository {

    suspend fun searchPodcasts(
        query: String,
        type: String,
        language: String = languageCode,
    ): Either<Failure, PodcastSearch>
}
