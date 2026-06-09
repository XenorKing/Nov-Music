package com.novmusic.data.api

import com.novmusic.data.model.JamendoTrack
import com.novmusic.data.model.JamendoTracksResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

private const val CLIENT_ID = "2956fe68"
private const val BASE_URL = "https://api.jamendo.com/v3.0"

@Singleton
class JamendoApiClient @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {

    suspend fun searchTracks(query: String, limit: Int = 30, offset: Int = 0): List<JamendoTrack> {
        val url = "$BASE_URL/tracks/" +
            "?client_id=$CLIENT_ID" +
            "&format=json" +
            "&search=${query.encodeUrl()}" +
            "&limit=$limit" +
            "&offset=$offset" +
            "&audioformat=mp31" +
            "&order=relevance_desc"
        return fetchTracks(url)
    }

    suspend fun getTrendingTracks(limit: Int = 50): List<JamendoTrack> {
        val url = "$BASE_URL/tracks/" +
            "?client_id=$CLIENT_ID" +
            "&format=json" +
            "&limit=$limit" +
            "&audioformat=mp31" +
            "&order=popularity_total"
        return fetchTracks(url)
    }

    private suspend fun fetchTracks(url: String): List<JamendoTrack> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return@withContext emptyList()

        val body = response.body?.string() ?: return@withContext emptyList()
        try {
            val parsed = json.decodeFromString<JamendoTracksResponse>(body)
            parsed.results.filter { !it.audio.isNullOrBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, "UTF-8")
}
