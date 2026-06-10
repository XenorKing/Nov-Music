package com.novmusic.data.api

import com.novmusic.data.model.JamendoTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLException

private const val CLIENT_ID = "2956fe68"
private const val BASE_URL = "https://api.jamendo.com/v3.0"

@Singleton
class JamendoApiClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {

    suspend fun searchTracks(query: String, limit: Int = 15, offset: Int = 0): List<JamendoTrack> {
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

    suspend fun getTrendingTracks(limit: Int = 20): List<JamendoTrack> {
        val url = "$BASE_URL/tracks/" +
            "?client_id=$CLIENT_ID" +
            "&format=json" +
            "&limit=$limit" +
            "&audioformat=mp31" +
            "&order=popularity_total"
        return fetchTracks(url)
    }

    private suspend fun fetchTracks(url: String): List<JamendoTrack> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "NovMusic/1.0 Android")
                .build()

            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string()

            if (!response.isSuccessful) {
                val preview = body?.take(200) ?: "(нет тела)"
                throw Exception("HTTP ${response.code} ${response.message} — ${preview}")
            }

            if (body.isNullOrBlank()) {
                throw Exception("Пустой ответ от сервера (HTTP ${response.code})")
            }

            if (!body.trimStart().startsWith('{')) {
                throw Exception("Не JSON: ${body.take(100)}")
            }

            parseTracksFromJson(body)
        } catch (e: UnknownHostException) {
            throw Exception("Нет доступа к api.jamendo.com — проверь интернет (${e.message})")
        } catch (e: SocketTimeoutException) {
            throw Exception("Таймаут: соединение прервано. Слабый сигнал? (${e.javaClass.simpleName})")
        } catch (e: SSLException) {
            throw Exception("SSL/TLS ошибка: ${e.message}")
        } catch (e: java.io.IOException) {
            throw Exception("Ошибка сети (${e.javaClass.simpleName}): ${e.message}")
        }
    }

    private fun parseTracksFromJson(body: String): List<JamendoTrack> {
        val root = JSONObject(body)
        val headers = root.optJSONObject("headers")
        val status = headers?.optString("status", "success") ?: "success"
        val errorMessage = headers?.optString("error_message", "") ?: ""
        if (status != "success") {
            throw Exception("Jamendo API: ${errorMessage} (status=${status})")
        }
        val results = root.optJSONArray("results") ?: return emptyList()
        val tracks = mutableListOf<JamendoTrack>()
        for (i in 0 until results.length()) {
            val obj = results.optJSONObject(i) ?: continue
            val audio = obj.optString("audio", "")
            val audioDownload = obj.optString("audiodownload", "")
            if (audio.isBlank() && audioDownload.isBlank()) continue
            tracks.add(
                JamendoTrack(
                    id = obj.optString("id", ""),
                    name = obj.optString("name", ""),
                    duration = obj.optInt("duration", 0),
                    artistName = obj.optString("artist_name", ""),
                    albumName = obj.optString("album_name", ""),
                    albumImage = obj.optString("album_image", "").ifBlank { null },
                    audio = audio.ifBlank { null },
                    audiodownload = audioDownload.ifBlank { null },
                    image = obj.optString("image", "").ifBlank { null },
                    shareUrl = obj.optString("shareurl", ""),
                    genre = obj.optString("genre", "").ifBlank { null },
                    listens = obj.optLong("listens", 0),
                    likesCount = obj.optLong("likes_count", 0)
                )
            )
        }
        return tracks
    }

    private fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, "UTF-8")
}