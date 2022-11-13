package com.ipsoft.ppp.domain.model

data class Episode(
    val id: String,
    val link: String,
    val audio: String,
    val image: String,
    val podcast: Podcast,
    val thumbnail: String,
    val pubDateMS: Long,
    val titleOriginal: String,
    val listenNotesURL: String,
    val audioLengthSec: Long,
    val explicitContent: Boolean,
    val descriptionOriginal: String,
)
