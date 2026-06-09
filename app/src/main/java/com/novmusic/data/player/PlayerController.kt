package com.novmusic.data.player

  import android.content.Context
  import androidx.media3.common.MediaItem
  import androidx.media3.common.Player
  import androidx.media3.exoplayer.ExoPlayer
  import com.novmusic.data.model.Track
  import dagger.hilt.android.qualifiers.ApplicationContext
  import kotlinx.coroutines.flow.MutableStateFlow
  import kotlinx.coroutines.flow.StateFlow
  import kotlinx.coroutines.flow.asStateFlow
  import javax.inject.Inject
  import javax.inject.Singleton

  data class PlayerState(
      val currentTrack: Track? = null,
      val isPlaying: Boolean = false,
      val currentPosition: Long = 0L,
      val duration: Long = 0L,
      val isLoading: Boolean = false,
      val error: String? = null
  )

  @Singleton
  class PlayerController @Inject constructor(
      @ApplicationContext private val context: Context,
      val exoPlayer: ExoPlayer
  ) {

      private val _playerState = MutableStateFlow(PlayerState())
      val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

      init {
          exoPlayer.addListener(object : Player.Listener {
              override fun onPlaybackStateChanged(playbackState: Int) {
                  _playerState.value = _playerState.value.copy(
                      isLoading = playbackState == Player.STATE_BUFFERING,
                      isPlaying = exoPlayer.isPlaying
                  )
              }

              override fun onIsPlayingChanged(isPlaying: Boolean) {
                  _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
              }

              override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                  _playerState.value = _playerState.value.copy(
                      error = "Ошибка воспроизведения: ${error.message}",
                      isLoading = false,
                      isPlaying = false
                  )
              }
          })
      }

      fun playTrack(track: Track) {
          val streamUrl = track.streamUrl
          if (streamUrl.isNullOrBlank()) {
              _playerState.value = _playerState.value.copy(error = "Трек недоступен для воспроизведения")
              return
          }
          // VK Audio URLs are self-authenticated, use directly
          val mediaItem = MediaItem.fromUri(streamUrl)
          exoPlayer.setMediaItem(mediaItem)
          exoPlayer.prepare()
          exoPlayer.play()
          _playerState.value = _playerState.value.copy(
              currentTrack = track,
              isPlaying = true,
              isLoading = true,
              error = null
          )
      }

      fun togglePlayPause() {
          if (exoPlayer.isPlaying) {
              exoPlayer.pause()
          } else {
              exoPlayer.play()
          }
      }

      fun seekTo(position: Long) {
          exoPlayer.seekTo(position)
      }

      fun getCurrentPosition(): Long = exoPlayer.currentPosition
      fun getDuration(): Long = exoPlayer.duration

      fun stop() {
          exoPlayer.stop()
          _playerState.value = PlayerState()
      }
  }
  