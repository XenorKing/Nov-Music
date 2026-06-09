package com.novmusic.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ---- Jamendo API models ----

@Serializable
data class JamendoSearchResponse(
    @SerialName("results") val results: List<JamendoTrack> = emptyList(),
    @SerialName("headers") val headers: JamendoHeaders? = null
)

@Serializable
data class JamendoTracksResponse(
    @SerialName("results") val results: List<JamendoTrack> = emptyList(),
    @SerialName("headers") val headers: JamendoHeaders? = null
)

@Serializable
data class JamendoHeaders(
    @SerialName("status") val status: String = "",
    @SerialName("code") val code: Int = 0,
    @SerialName("results_count") val resultsCount: Int = 0
)

@Serializable
data class JamendoTrack(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("duration") val duration: Int = 0,
    @SerialName("artist_name") val artistName: String = "",
    @SerialName("album_name") val albumName: String = "",
    @SerialName("album_image") val albumImage: String? = null,
    @SerialName("audio") val audio: String? = null,
    @SerialName("audiodownload") val audiodownload: String? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("shareurl") val shareUrl: String = "",
    @SerialName("genre") val genre: String? = null,
    @SerialName("listens") val listens: Long = 0,
    @SerialName("likes_count") val likesCount: Long = 0
)

// ---- App domain model ----

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val artworkUrl: String?,
    val streamUrl: String?,
    val permalinkUrl: String,
    val genre: String?,
    val playbackCount: Long,
    val likesCount: Long,
    val isSaved: Boolean = false
)

fun JamendoTrack.toTrack(): Track = Track(
    id = id,
    title = name,
    artist = artistName,
    duration = duration * 1000L,
    artworkUrl = albumImage ?: image,
    streamUrl = audio ?: audiodownload,
    permalinkUrl = shareUrl,
    genre = genre,
    playbackCount = listens,
    likesCount = likesCount
)

// ---- Firestore saved track ----

data class SavedTrack(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val artworkUrl: String? = null,
    val streamUrl: String? = null,
    val permalinkUrl: String = "",
    val genre: String? = null,
    val duration: Long = 0,
    val savedAt: Long = System.currentTimeMillis()
)

fun SavedTrack.toTrack(): Track = Track(
    id = id,
    title = title,
    artist = artist,
    duration = duration,
    artworkUrl = artworkUrl,
    streamUrl = streamUrl,
    permalinkUrl = permalinkUrl,
    genre = genre,
    playbackCount = 0,
    likesCount = 0,
    isSaved = true
)

fun Track.toSavedTrack(): SavedTrack = SavedTrack(
    id = id,
    title = title,
    artist = artist,
    artworkUrl = artworkUrl,
    streamUrl = streamUrl,
    permalinkUrl = permalinkUrl,
    genre = genre,
    duration = duration
)
