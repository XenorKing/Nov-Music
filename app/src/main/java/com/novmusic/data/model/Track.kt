package com.novmusic.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VkAudioResponse(
    val response: VkAudioItems? = null,
    val error: VkError? = null
)

@Serializable
data class VkPopularResponse(
    val response: List<VkAudio> = emptyList(),
    val error: VkError? = null
)

@Serializable
data class VkAudioItems(
    val count: Int = 0,
    val items: List<VkAudio> = emptyList()
)

@Serializable
data class VkAudio(
    val id: Long = 0,
    @SerialName("owner_id") val ownerId: Long = 0,
    val artist: String = "",
    val title: String = "",
    val duration: Int = 0,
    val url: String = "",
    val album: VkAlbum? = null,
    @SerialName("is_explicit") val isExplicit: Boolean = false
)

@Serializable
data class VkAlbum(
    val id: Long = 0,
    val title: String = "",
    val thumb: VkThumb? = null
)

@Serializable
data class VkThumb(
    @SerialName("photo_68") val photo68: String? = null,
    @SerialName("photo_300") val photo300: String? = null,
    @SerialName("photo_600") val photo600: String? = null
)

@Serializable
data class VkError(
    @SerialName("error_code") val errorCode: Int = 0,
    @SerialName("error_msg") val errorMsg: String = ""
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

fun VkAudio.toTrack(): Track = Track(
    id = "${ownerId}_${id}",
    title = title,
    artist = artist,
    duration = duration * 1000L,
    artworkUrl = album?.thumb?.photo300 ?: album?.thumb?.photo68,
    streamUrl = url.takeIf { it.isNotBlank() },
    permalinkUrl = "https://vk.com/audio${ownerId}_${id}",
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
