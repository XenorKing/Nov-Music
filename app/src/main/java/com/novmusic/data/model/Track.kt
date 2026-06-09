package com.novmusic.data.model

  import kotlinx.serialization.SerialName
  import kotlinx.serialization.Serializable

  // iTunes API models
  @Serializable
  data class ItunesSearchResponse(
      @SerialName("resultCount") val resultCount: Int = 0,
      @SerialName("results") val results: List<ItunesTrack> = emptyList()
  )

  @Serializable
  data class ItunesTrack(
      @SerialName("trackId") val trackId: Long = 0,
      @SerialName("trackName") val trackName: String = "",
      @SerialName("artistName") val artistName: String = "",
      @SerialName("collectionName") val collectionName: String? = null,
      @SerialName("previewUrl") val previewUrl: String? = null,
      @SerialName("artworkUrl100") val artworkUrl100: String? = null,
      @SerialName("artworkUrl60") val artworkUrl60: String? = null,
      @SerialName("trackTimeMillis") val trackTimeMillis: Long? = null,
      @SerialName("primaryGenreName") val primaryGenreName: String? = null,
      @SerialName("trackViewUrl") val trackViewUrl: String? = null
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

  fun ItunesTrack.toTrack(): Track = Track(
      id = trackId.toString(),
      title = trackName,
      artist = artistName,
      duration = trackTimeMillis ?: 30000L,
      artworkUrl = artworkUrl100?.replace("100x100", "300x300") ?: artworkUrl100,
      streamUrl = previewUrl,
      permalinkUrl = trackViewUrl ?: "",
      genre = primaryGenreName,
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
  