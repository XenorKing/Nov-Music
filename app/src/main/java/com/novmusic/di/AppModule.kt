package com.novmusic.di

  import android.content.Context
  import androidx.media3.exoplayer.ExoPlayer
  import com.google.firebase.auth.FirebaseAuth
  import com.google.firebase.firestore.FirebaseFirestore
  import com.novmusic.data.api.ItunesMusicApi
  import dagger.Module
  import dagger.Provides
  import dagger.hilt.InstallIn
  import dagger.hilt.android.qualifiers.ApplicationContext
  import dagger.hilt.components.SingletonComponent
  import kotlinx.serialization.json.Json
  import okhttp3.MediaType.Companion.toMediaType
  import okhttp3.OkHttpClient
  import okhttp3.logging.HttpLoggingInterceptor
  import retrofit2.Retrofit
  import retrofit2.converter.kotlinx.serialization.asConverterFactory
  import javax.inject.Singleton

  @Module
  @InstallIn(SingletonComponent::class)
  object AppModule {

      private const val ITUNES_BASE_URL = "https://itunes.apple.com/"

      @Provides
      @Singleton
      fun provideJson(): Json = Json {
          ignoreUnknownKeys = true
          coerceInputValues = true
          isLenient = true
      }

      @Provides
      @Singleton
      fun provideOkHttpClient(): OkHttpClient {
          return OkHttpClient.Builder()
              .addInterceptor(HttpLoggingInterceptor().apply {
                  level = HttpLoggingInterceptor.Level.BASIC
              })
              .build()
      }

      @Provides
      @Singleton
      fun provideItunesMusicApi(okHttpClient: OkHttpClient, json: Json): ItunesMusicApi {
          return Retrofit.Builder()
              .baseUrl(ITUNES_BASE_URL)
              .client(okHttpClient)
              .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
              .build()
              .create(ItunesMusicApi::class.java)
      }

      @Provides
      @Singleton
      fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

      @Provides
      @Singleton
      fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

      @Provides
      @Singleton
      fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
          return ExoPlayer.Builder(context).build()
      }
  }
  