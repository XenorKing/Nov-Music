package com.novmusic.data.api

import com.novmusic.data.model.DeezerSearchResponse
import com.novmusic.data.model.DeezerChartsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerMusicApi {

    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 30,
        @Query("index") offset: Int = 0
    ): DeezerSearchResponse

    @GET("chart/0/tracks")
    suspend fun getTopTracks(
        @Query("limit") limit: Int = 30
    ): DeezerChartsResponse
}
