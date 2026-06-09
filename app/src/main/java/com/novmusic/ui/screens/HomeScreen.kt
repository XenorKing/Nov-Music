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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MusicNote
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
import com.novmusic.ui.viewmodel.AuthState

@Composable
fun HomeScreen(
    musicViewModel: MusicViewModel,
    authViewModel: AuthViewModel
) {
    val uiState by musicViewModel.uiState.collectAsState()
    val playerState by musicViewModel.playerState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val userName = (authState as? AuthState.Authenticated)?.user?.displayName ?: "Слушатель"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (playerState.currentTrack != null) 80.dp else 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PrimaryPurple.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Привет, $userName",
                                color = OnSurfaceVariantDark,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "novМузыка",
                                color = OnSurfaceDark,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { authViewModel.signOut() }) {
                            Icon(Icons.Default.Logout, null, tint = OnSurfaceVariantDark)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Популярное",
                    color = OnSurfaceDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            item {
                if (uiState.isTrendingLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryPurple)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.trendingTracks.take(10)) { track ->
                            TrendingTrackCard(
                                track = track,
                                isSaved = uiState.savedTrackIds.contains(track.id),
                                isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying,
                                onClick = { musicViewModel.playTrack(track) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Все популярные",
                    color = OnSurfaceDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
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
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        uiState.saveMessage?.let { msg ->
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(2000)
                musicViewModel.clearSaveMessage()
            }
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp, start = 16.dp, end = 16.dp),
                containerColor = SurfaceVariantDark,
                contentColor = OnSurfaceDark
            ) {
                Text(msg)
            }
        }
    }
}

@Composable
fun TrendingTrackCard(
    track: Track,
    isSaved: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark)
    ) {
        Column {
            Box(modifier = Modifier.size(150.dp)) {
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
                            .background(Brush.linearGradient(listOf(PrimaryPurple, SecondaryPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryPurple.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", color = Color.White, fontSize = 32.sp)
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = track.title,
                    color = OnSurfaceDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist,
                    color = OnSurfaceVariantDark,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

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
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
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
                        .background(Brush.linearGradient(listOf(PrimaryPurple, SecondaryPink))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MusicNote, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryPurple.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(
                text = track.title,
                color = if (isPlaying) PrimaryPurple else OnSurfaceDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                color = OnSurfaceVariantDark,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(onClick = onSaveToggle) {
            Text(
                text = if (isSaved) "♥" else "♡",
                color = if (isSaved) SecondaryPink else OnSurfaceVariantDark,
                fontSize = 20.sp
            )
        }
    }
}
