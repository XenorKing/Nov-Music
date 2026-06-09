package com.novmusic.data.api

import com.novmusic.data.model.SoundCloudSearchResponse
import com.novmusic.data.model.SoundCloudTrack
import retrofit2.http.GET
import retrofit2.http.Query

interface SoundCloudApi {

    @GET("search/tracks")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("linked_partitioning") linkedPartitioning: Int = 1
    ): SoundCloudSearchResponse

    @GET("tracks")
    suspend fun getTrendingTracks(
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "hotness",
        @Query("linked_partitioning") linkedPartitioning: Int = 1
    ): SoundCloudSearchResponse

    @GET("tracks")
    suspend fun getTracksByGenre(
        @Query("genres") genre: String,
        @Query("limit") limit: Int = 20,
        @Query("linked_partitioning") linkedPartitioning: Int = 1
    ): SoundCloudSearchResponse
}
