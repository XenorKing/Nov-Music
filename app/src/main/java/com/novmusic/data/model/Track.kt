package com.novmusic.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Deezer API models
@Serializable
data class DeezerSearchResponse(
    @SerialName("data") val data: List<DeezerTrack> = emptyList(),
    @SerialName("total") val total: Int = 0
)

@Serializable
data class DeezerChartsResponse(
    @SerialName("data") val data: List<DeezerTrack> = emptyList()
)

@Serializable
data class DeezerTrack(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("preview") val preview: String? = null,
    @SerialName("duration") val duration: Int = 0,
    @SerialName("artist") val artist: DeezerArtist = DeezerArtist(),
    @SerialName("album") val album: DeezerAlbum = DeezerAlbum()
)

@Serializable
data class DeezerArtist(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = ""
)

@Serializable
data class DeezerAlbum(
    @SerialName("id") val id: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("cover_medium") val coverMedium: String? = null,
    @SerialName("cover_big") val coverBig: String? = null
)

// App domain model
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

fun DeezerTrack.toTrack(): Track = Track(
    id = id.toString(),
    title = title,
    artist = artist.name,
    duration = duration * 1000L,
    artworkUrl = album.coverBig ?: album.coverMedium,
    streamUrl = preview,
    permalinkUrl = "",
    genre = null,
    playbackCount = 0,
    likesCount = 0
)

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
