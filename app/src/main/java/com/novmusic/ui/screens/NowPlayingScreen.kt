package com.novmusic.ui.screens

  import android.os.Build
  import androidx.compose.animation.animateColorAsState
  import androidx.compose.animation.core.tween
  import androidx.compose.foundation.background
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.shape.CircleShape
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.Bookmark
  import androidx.compose.material.icons.filled.BookmarkBorder
  import androidx.compose.material.icons.filled.KeyboardArrowDown
  import androidx.compose.material.icons.filled.MusicNote
  import androidx.compose.material.icons.filled.Pause
  import androidx.compose.material.icons.filled.PlayArrow
  import androidx.compose.material.icons.filled.SkipNext
  import androidx.compose.material.icons.filled.SkipPrevious
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.blur
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.Brush
  import androidx.compose.ui.graphics.Color
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.platform.LocalContext
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextOverflow
  import androidx.compose.ui.unit.dp
  import androidx.compose.ui.unit.sp
  import coil.compose.AsyncImage
  import com.novmusic.ui.theme.*
  import com.novmusic.ui.viewmodel.MusicViewModel
  import java.util.concurrent.TimeUnit

  @Composable
  fun NowPlayingScreen(
      musicViewModel: MusicViewModel,
      onBack: () -> Unit
  ) {
      val uiState by musicViewModel.uiState.collectAsState()
      val playerState by musicViewModel.playerState.collectAsState()
      val track = playerState.currentTrack ?: return
      val isSaved = uiState.savedTrackIds.contains(track.id)

      Box(
          modifier = Modifier.fillMaxSize().background(Color(0xFF0D0D1A))
      ) {
          // Blurred artwork background
          if (track.artworkUrl != null) {
              AsyncImage(
                  model = track.artworkUrl,
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                      .fillMaxSize()
                      .then(
                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                              Modifier.blur(48.dp)
                          else
                              Modifier
                      )
              )
          }

          // Dark gradient overlay
          Box(
              modifier = Modifier
                  .fillMaxSize()
                  .background(
                      Brush.verticalGradient(
                          colors = listOf(
                              Color(0xCC0D0D1A),
                              Color(0xEE0D0D1A),
                              Color(0xFF0D0D1A)
                          )
                      )
                  )
          )

          // Content
          Column(
              modifier = Modifier
                  .fillMaxSize()
                  .systemBarsPadding()
                  .padding(horizontal = 28.dp),
              horizontalAlignment = Alignment.CenterHorizontally
          ) {
              // Top bar
              Row(
                  modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
              ) {
                  IconButton(onClick = onBack) {
                      Icon(
                          Icons.Default.KeyboardArrowDown,
                          contentDescription = "Назад",
                          tint = Color.White,
                          modifier = Modifier.size(32.dp)
                      )
                  }
                  Text(
                      text = "Сейчас играет",
                      color = Color.White.copy(alpha = 0.7f),
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier.weight(1f).wrapContentWidth()
                  )
                  IconButton(onClick = { musicViewModel.toggleSaveTrack(track) }) {
                      Icon(
                          imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                          contentDescription = null,
                          tint = if (isSaved) PrimaryPurple else Color.White,
                          modifier = Modifier.size(24.dp)
                      )
                  }
              }

              Spacer(modifier = Modifier.weight(1f))

              // Artwork
              Box(
                  modifier = Modifier
                      .size(280.dp)
                      .clip(RoundedCornerShape(24.dp)),
                  contentAlignment = Alignment.Center
              ) {
                  if (track.artworkUrl != null) {
                      AsyncImage(
                          model = track.artworkUrl,
                          contentDescription = null,
                          modifier = Modifier.fillMaxSize(),
                          contentScale = ContentScale.Crop
                      )
                  } else {
                      Box(
                          modifier = Modifier
                              .fillMaxSize()
                              .background(Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan.copy(alpha = 0.7f)))),
                          contentAlignment = Alignment.Center
                      ) {
                          Icon(Icons.Default.MusicNote, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(96.dp))
                      }
                  }
              }

              Spacer(modifier = Modifier.weight(1f))

              // Title + artist
              Column(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalAlignment = Alignment.Start
              ) {
                  Text(
                      text = track.title,
                      color = Color.White,
                      fontSize = 22.sp,
                      fontWeight = FontWeight.Bold,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                      text = track.artist,
                      color = Color.White.copy(alpha = 0.65f),
                      fontSize = 15.sp,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                  )
              }

              Spacer(modifier = Modifier.height(28.dp))

              // Progress bar
              val progress = if (playerState.duration > 0) playerState.currentPosition.toFloat() / playerState.duration.toFloat() else 0f

              Column(modifier = Modifier.fillMaxWidth()) {
                  Slider(
                      value = progress,
                      onValueChange = {},
                      modifier = Modifier.fillMaxWidth(),
                      colors = SliderDefaults.colors(
                          thumbColor = Color.White,
                          activeTrackColor = PrimaryPurple,
                          inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                      )
                  )
                  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                      Text(formatDurationNP(playerState.currentPosition), color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
                      Text(formatDurationNP(playerState.duration), color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp)
                  }
              }

              Spacer(modifier = Modifier.height(20.dp))

              // Controls
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceEvenly,
                  verticalAlignment = Alignment.CenterVertically
              ) {
                  IconButton(onClick = { /* prev */ }, modifier = Modifier.size(52.dp)) {
                      Icon(Icons.Default.SkipPrevious, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                  }

                  Box(
                      modifier = Modifier
                          .size(68.dp)
                          .background(PrimaryPurple, CircleShape),
                      contentAlignment = Alignment.Center
                  ) {
                      if (playerState.isLoading) {
                          CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                      } else {
                          IconButton(onClick = { musicViewModel.togglePlayPause() }, modifier = Modifier.size(68.dp)) {
                              Icon(
                                  imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                  contentDescription = null,
                                  tint = Color.White,
                                  modifier = Modifier.size(36.dp)
                              )
                          }
                      }
                  }

                  IconButton(onClick = { /* next */ }, modifier = Modifier.size(52.dp)) {
                      Icon(Icons.Default.SkipNext, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                  }
              }

              Spacer(modifier = Modifier.height(40.dp))
          }
      }
  }

  private fun formatDurationNP(millis: Long): String {
      if (millis <= 0) return "0:00"
      val min = TimeUnit.MILLISECONDS.toMinutes(millis)
      val sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
      return "${min}:${sec.toString().padStart(2, '0')}"
  }
  