package com.novmusic.data.api

import com.novmusic.data.model.VkAudioResponse
import com.novmusic.data.model.VkPopularResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface VkMusicApi {

    @GET("audio.search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("access_token") token: String,
        @Query("count") count: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("v") version: String = "5.131"
    ): VkAudioResponse

    @GET("audio.getPopular")
    suspend fun getPopularTracks(
        @Query("access_token") token: String,
        @Query("count") count: Int = 30,
        @Query("genre_id") genreId: Int = 0,
        @Query("v") version: String = "5.131"
    ): VkPopularResponse

    @GET("audio.get")
    suspend fun getUserTracks(
        @Query("access_token") token: String,
        @Query("count") count: Int = 30,
        @Query("v") version: String = "5.131"
    ): VkAudioResponse
}
