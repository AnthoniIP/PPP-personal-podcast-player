package com.ipsoft.ppp.domain.model

import com.ipsoft.ppp.domain.model.Episode

data class PodcastSearch(
    val count: Long,
    val total: Long,
    val results: List<Episode>
)