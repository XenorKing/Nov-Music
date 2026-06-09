package com.novmusic.data.api

import com.novmusic.data.model.JamendoSearchResponse
import com.novmusic.data.model.JamendoTracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoMusicApi {

    @GET("tracks/")
    suspend fun searchTracks(
        @Query("client_id") clientId: String = "2956fe68",
        @Query("format") format: String = "json",
        @Query("search") search: String,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("include") include: String = "musicinfo",
        @Query("audioformat") audioformat: String = "mp32",
        @Query("order") order: String = "relevance_desc"
    ): JamendoSearchResponse

    @GET("tracks/")
    suspend fun getTrendingTracks(
        @Query("client_id") clientId: String = "2956fe68",
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 50,
        @Query("order") order: String = "popularity_total_desc",
        @Query("include") include: String = "musicinfo",
        @Query("audioformat") audioformat: String = "mp32"
    ): JamendoTracksResponse
}
