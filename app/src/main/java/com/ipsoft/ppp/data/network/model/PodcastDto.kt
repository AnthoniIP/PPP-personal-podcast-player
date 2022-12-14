package com.ipsoft.ppp.data.network.model

import com.google.gson.annotations.SerializedName
import com.ipsoft.ppp.domain.model.Podcast

data class PodcastDto(
    val id: String,
    val image: String,
    val thumbnail: String,
    @SerializedName("title_original")
    val titleOriginal: String,
    @SerializedName("listennotes_url")
    val listenNotesURL: String,
    @SerializedName("publisher_original")
    val publisherOriginal: String
) {

    fun asDomainModel() = Podcast(
        id,
        image,
        thumbnail,
        titleOriginal,
        listenNotesURL,
        publisherOriginal
    )
}
