package com.novmusic.ui.screens

  import androidx.compose.foundation.background
  import androidx.compose.foundation.clickable
  import androidx.compose.foundation.layout.*
  import androidx.compose.foundation.lazy.LazyColumn
  import androidx.compose.foundation.lazy.LazyRow
  import androidx.compose.foundation.lazy.items
  import androidx.compose.foundation.shape.CircleShape
  import androidx.compose.foundation.shape.RoundedCornerShape
  import androidx.compose.material.icons.Icons
  import androidx.compose.material.icons.filled.Bookmark
  import androidx.compose.material.icons.filled.BookmarkBorder
  import androidx.compose.material.icons.filled.MusicNote
  import androidx.compose.material.icons.filled.Pause
  import androidx.compose.material.icons.filled.PlayArrow
  import androidx.compose.material3.*
  import androidx.compose.runtime.*
  import androidx.compose.ui.Alignment
  import androidx.compose.ui.Modifier
  import androidx.compose.ui.draw.clip
  import androidx.compose.ui.graphics.Brush
  import androidx.compose.ui.graphics.Color
  import androidx.compose.ui.layout.ContentScale
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextOverflow
  import androidx.compose.ui.unit.dp
  import androidx.compose.ui.unit.sp
  import coil.compose.AsyncImage
  import com.novmusic.data.model.Track
  import com.novmusic.ui.components.MiniPlayer
  import com.novmusic.ui.theme.*
  import com.novmusic.ui.viewmodel.MusicViewModel
  import java.util.concurrent.TimeUnit

  @Composable
  fun TrackListItem(
      track: Track,
      isSaved: Boolean,
      isPlaying: Boolean,
      onClick: () -> Unit,
      onSaveToggle: () -> Unit
  ) {
      Row(
          modifier = Modifier
              .fillMaxWidth()
              .clickable(onClick = onClick)
              .padding(horizontal = 16.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
      ) {
          Box(
              modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(SurfaceVariantDark),
              contentAlignment = Alignment.Center
          ) {
              if (track.artworkUrl != null) {
                  AsyncImage(model = track.artworkUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
              } else {
                  Icon(Icons.Default.MusicNote, null, tint = OnSurfaceVariantDark, modifier = Modifier.size(24.dp))
              }
              if (isPlaying) {
                  Box(modifier = Modifier.fillMaxSize().background(PrimaryPurple.copy(alpha = 0.6f)), contentAlignment = Alignment.Center) {
                      Icon(Icons.Default.Pause, null, tint = Color.White, modifier = Modifier.size(22.dp))
                  }
              }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
              Text(
                  text = track.title,
                  color = if (isPlaying) PrimaryPurple else OnSurfaceDark,
                  fontSize = 14.sp,
                  fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal,
                  maxLines = 1, overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(2.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = track.artist, color = OnSurfaceVariantDark, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                  if (track.duration > 0) {
                      Text(text = "  ·  " + formatDuration(track.duration), color = OnSurfaceVariantDark.copy(alpha = 0.6f), fontSize = 11.sp)
                  }
              }
          }
          IconButton(onClick = onSaveToggle, modifier = Modifier.size(36.dp)) {
              Icon(
                  imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                  contentDescription = null,
                  tint = if (isSaved) PrimaryPurple else OnSurfaceVariantDark,
                  modifier = Modifier.size(20.dp)
              )
          }
      }
  }

  private fun formatDuration(millis: Long): String {
      val min = TimeUnit.MILLISECONDS.toMinutes(millis)
      val sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
      return "${min}:${sec.toString().padStart(2, '0')}"
  }

  @Composable
  fun HomeScreen(musicViewModel: MusicViewModel) {
      val uiState by musicViewModel.uiState.collectAsState()
      val playerState by musicViewModel.playerState.collectAsState()

      Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
          LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp)
          ) {
              item {
                  Box(
                      modifier = Modifier.fillMaxWidth()
                          .background(Brush.verticalGradient(colors = listOf(PrimaryPurple.copy(alpha = 0.25f), Color.Transparent)))
                          .padding(horizontal = 24.dp, vertical = 28.dp)
                  ) {
                      Column {
                          Text("Добро пожаловать", color = OnSurfaceVariantDark, fontSize = 13.sp)
                          Spacer(modifier = Modifier.height(2.dp))
                          Text("NovMusic", color = OnSurfaceDark, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                      }
                  }
              }
              item {
                  Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                      Text("Популярное", color = OnSurfaceDark, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                      if (uiState.isTrendingLoading) CircularProgressIndicator(color = PrimaryPurple, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                  }
              }
              item {
                  if (uiState.trendingTracks.isEmpty() && !uiState.isTrendingLoading) {
                      Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                          Column(horizontalAlignment = Alignment.CenterHorizontally) {
                              Icon(Icons.Default.MusicNote, null, tint = OnSurfaceVariantDark.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                              Spacer(modifier = Modifier.height(8.dp))
                              Text("Треки загружаются...", color = OnSurfaceVariantDark.copy(alpha = 0.6f), fontSize = 13.sp)
                          }
                      }
                  } else if (!uiState.isTrendingLoading) {
                      LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                          items(uiState.trendingTracks.take(10)) { track ->
                              TrendingTrackCard(track = track, isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying, onClick = { musicViewModel.playTrack(track) })
                          }
                      }
                  }
              }
              item {
                  Spacer(modifier = Modifier.height(20.dp))
                  Text("Все популярные", color = OnSurfaceDark, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
              }
              items(uiState.trendingTracks) { track ->
                  TrackListItem(track = track, isSaved = uiState.savedTrackIds.contains(track.id), isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying, onClick = { musicViewModel.playTrack(track) }, onSaveToggle = { musicViewModel.toggleSaveTrack(track) })
              }
          }
          if (playerState.currentTrack != null) {
              MiniPlayer(playerState = playerState, onPlayPause = { musicViewModel.togglePlayPause() }, modifier = Modifier.align(Alignment.BottomCenter))
          }
          uiState.saveMessage?.let { msg ->
              LaunchedEffect(msg) { kotlinx.coroutines.delay(2000); musicViewModel.clearSaveMessage() }
              Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp, start = 16.dp, end = 16.dp), containerColor = SurfaceVariantDark, contentColor = OnSurfaceDark) { Text(msg) }
          }
      }
  }

  @Composable
  fun TrendingTrackCard(track: Track, isPlaying: Boolean, onClick: () -> Unit) {
      Card(modifier = Modifier.width(148.dp).clickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = SurfaceCard)) {
          Column {
              Box(modifier = Modifier.size(148.dp)) {
                  if (track.artworkUrl != null) {
                      AsyncImage(model = track.artworkUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                  } else {
                      Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan.copy(alpha = 0.7f)))), contentAlignment = Alignment.Center) {
                          Icon(Icons.Default.MusicNote, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(48.dp))
                      }
                  }
                  if (isPlaying) {
                      Box(modifier = Modifier.fillMaxSize().background(PrimaryPurple.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                          Box(modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.15f), CircleShape), contentAlignment = Alignment.Center) {
                              Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(26.dp))
                          }
                      }
                  }
              }
              Column(modifier = Modifier.padding(10.dp)) {
                  Text(track.title, color = OnSurfaceDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(track.artist, color = OnSurfaceVariantDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
              }
          }
      }
  }
  