package com.ipsoft.ppp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.ipsoft.ppp.domain.model.PodcastSearch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Instant

const val SECONDS_TO_REFRESH = 5L

class PodcastDataStore(
    private val context: Context,
) {
    private val lastAPIFetchMillis = longPreferencesKey("last_api_fetch_millis")
    private val podcastSearchResult = stringPreferencesKey("podcast_search_result")

    suspend fun storePodcastSearchResult(data: PodcastSearch) {
        context.podcastDataStore.edit { preferences ->
            val jsonString = Gson().toJson(data)
            preferences[lastAPIFetchMillis] = Instant.now().toEpochMilli()
            preferences[podcastSearchResult] = jsonString
        }
    }

    suspend fun readLastPodcastSearchResult(): PodcastSearch {
        return context.podcastDataStore.data.map { preferences ->
            val jsonString = preferences[podcastSearchResult]
            Gson().fromJson(jsonString, PodcastSearch::class.java)
        }.first()
    }

    suspend fun canFetchAPI(): Boolean {
        return context.podcastDataStore.data.map { preferences ->
            val epochMillis = preferences[lastAPIFetchMillis]

            return@map if (epochMillis != null) {
                val minDiffMillis = SECONDS_TO_REFRESH * 60 * 1000L
                val now = Instant.now().toEpochMilli()
                (now - minDiffMillis) > epochMillis
            } else {
                true
            }
        }.first()
    }
}

private val Context.podcastDataStore: DataStore<Preferences> by preferencesDataStore("podcasts")
