package com.novmusic.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SoundCloudSearchResponse(
    val collection: List<SoundCloudTrack> = emptyList(),
    @SerialName("next_href") val nextHref: String? = null,
    @SerialName("total_results") val totalResults: Int = 0
)

@Serializable
data class SoundCloudTrack(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val duration: Long = 0,
    @SerialName("stream_url") val streamUrl: String? = null,
    @SerialName("permalink_url") val permalinkUrl: String = "",
    @SerialName("artwork_url") val artworkUrl: String? = null,
    val user: SoundCloudUser = SoundCloudUser(),
    val genre: String? = null,
    @SerialName("playback_count") val playbackCount: Long = 0,
    @SerialName("likes_count") val likesCount: Long = 0,
    val streamable: Boolean = false,
    val kind: String = ""
)

@Serializable
data class SoundCloudUser(
    val id: Long = 0,
    val username: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("permalink_url") val permalinkUrl: String = ""
)

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

fun SoundCloudTrack.toTrack(): Track = Track(
    id = id.toString(),
    title = title,
    artist = user.username,
    duration = duration,
    artworkUrl = artworkUrl?.replace("large", "t500x500"),
    streamUrl = streamUrl,
    permalinkUrl = permalinkUrl,
    genre = genre,
    playbackCount = playbackCount,
    likesCount = likesCount
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
