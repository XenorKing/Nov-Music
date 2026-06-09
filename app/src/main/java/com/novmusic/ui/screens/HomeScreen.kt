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
import androidx.compose.material.icons.filled.History
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
import com.novmusic.ui.viewmodel.AuthViewModel
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

fun formatDuration(millis: Long): String {
    val min = TimeUnit.MILLISECONDS.toMinutes(millis)
    val sec = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return "${min}:${sec.toString().padStart(2, '0')}"
}

@Composable
fun HomeScreen(
    musicViewModel: MusicViewModel,
    authViewModel: AuthViewModel,
    onOpenPlayer: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val uiState by musicViewModel.uiState.collectAsState()
    val playerState by musicViewModel.playerState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp)
        ) {
            // Header with avatar + nickname
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(colors = listOf(PrimaryPurple.copy(alpha = 0.25f), Color.Transparent)))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan)),
                                    shape = CircleShape
                                )
                                .clickable { onOpenProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = authState.nickname.firstOrNull()?.uppercaseChar()?.toString()
                            if (initial != null) {
                                Text(initial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = authState.nickname.ifBlank { "Пользователь" },
                                color = OnSurfaceDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(text = "novМузыка", color = OnSurfaceVariantDark, fontSize = 12.sp)
                        }
                    }
                }
            }

            // History section
            if (uiState.historyTracks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.History, null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Недавно слушал", color = OnSurfaceDark, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.historyTracks.take(10)) { track ->
                            HistoryTrackChip(
                                track = track,
                                isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying,
                                onClick = { musicViewModel.playTrack(track) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Trending header
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Популярное", color = OnSurfaceDark, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    if (uiState.isTrendingLoading) CircularProgressIndicator(color = PrimaryPurple, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                }
            }

            // Trending horizontal row
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
                    LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(uiState.trendingTracks.take(10)) { track ->
                            TrendingTrackCard(
                                track = track,
                                isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying,
                                onClick = { musicViewModel.playTrack(track) }
                            )
                        }
                    }
                }
            }

            // All trending list
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Все популярные", color = OnSurfaceDark, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp))
            }
            items(uiState.trendingTracks) { track ->
                TrackListItem(
                    track = track,
                    isSaved = uiState.savedTrackIds.contains(track.id),
                    isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying,
                    onClick = { musicViewModel.playTrack(track) },
                    onSaveToggle = { musicViewModel.toggleSaveTrack(track) }
                )
            }
        }

        if (playerState.currentTrack != null) {
            MiniPlayer(
                playerState = playerState,
                onPlayPause = { musicViewModel.togglePlayPause() },
                onOpenPlayer = onOpenPlayer,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        uiState.saveMessage?.let { msg ->
            LaunchedEffect(msg) { kotlinx.coroutines.delay(2000); musicViewModel.clearSaveMessage() }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp, start = 16.dp, end = 16.dp),
                containerColor = SurfaceVariantDark,
                contentColor = OnSurfaceDark
            ) { Text(msg) }
        }
    }
}

@Composable
fun HistoryTrackChip(track: Track, isPlaying: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(
                if (isPlaying) PrimaryPurple.copy(alpha = 0.2f) else SurfaceVariantDark,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            if (track.artworkUrl != null) {
                AsyncImage(model = track.artworkUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Icon(Icons.Default.MusicNote, null, tint = OnSurfaceVariantDark, modifier = Modifier.size(18.dp))
            }
            if (isPlaying) {
                Box(modifier = Modifier.fillMaxSize().background(PrimaryPurple.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Pause, null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.widthIn(max = 110.dp)) {
            Text(track.title, color = if (isPlaying) PrimaryPurple else OnSurfaceDark, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(track.artist, color = OnSurfaceVariantDark, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun TrendingTrackCard(track: Track, isPlaying: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(148.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
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
