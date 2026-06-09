package com.novmusic.data.api

  import com.novmusic.data.model.ItunesSearchResponse
  import retrofit2.http.GET
  import retrofit2.http.Query

  interface ItunesMusicApi {

      @GET("search")
      suspend fun searchTracks(
          @Query("term") term: String,
          @Query("media") media: String = "music",
          @Query("entity") entity: String = "song",
          @Query("limit") limit: Int = 30,
          @Query("offset") offset: Int = 0
      ): ItunesSearchResponse

      @GET("search")
      suspend fun getTopTracks(
          @Query("term") term: String = "top hits 2024",
          @Query("media") media: String = "music",
          @Query("entity") entity: String = "song",
          @Query("limit") limit: Int = 30
      ): ItunesSearchResponse
  }
  