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
import com.novmusic.ui.viewmodel.AuthState
import com.novmusic.ui.viewmodel.AuthViewModel
import com.novmusic.ui.viewmodel.MusicViewModel

@Composable
fun HomeScreen(
    musicViewModel: MusicViewModel,
    authViewModel: AuthViewModel,
    onNavigateToVkAuth: () -> Unit = {}
) {
    val uiState by musicViewModel.uiState.collectAsState()
    val playerState by musicViewModel.playerState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val hasVkToken by authViewModel.hasVkToken.collectAsState()
    val userName = (authState as? AuthState.Authenticated)?.user?.displayName ?: "Слушатель"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (playerState.currentTrack != null) 90.dp else 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    PrimaryPurple.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 28.dp)
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
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "novМузыка",
                                color = OnSurfaceDark,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceVariantDark)
                                .clickable { authViewModel.signOut() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Logout,
                                null,
                                tint = OnSurfaceVariantDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (!hasVkToken) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A40))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Подключи ВКонтакте",
                                    color = OnSurfaceDark,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Нужно для доступа к музыке",
                                    color = OnSurfaceVariantDark,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { onNavigateToVkAuth() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90D9)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Войти", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Популярное",
                        color = OnSurfaceDark,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.isTrendingLoading) {
                        CircularProgressIndicator(
                            color = PrimaryPurple,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            item {
                if (uiState.trendingTracks.isEmpty() && !uiState.isTrendingLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.MusicNote,
                                null,
                                tint = OnSurfaceVariantDark.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (!hasVkToken) "Войдите через VK для музыки" else "Треки не найдены",
                                color = OnSurfaceVariantDark.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else if (!uiState.isTrendingLoading) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
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

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Все популярные",
                    color = OnSurfaceDark,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
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
                    .padding(
                        bottom = if (playerState.currentTrack != null) 90.dp else 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
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
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(148.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column {
            Box(modifier = Modifier.size(148.dp)) {
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
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryPurple, AccentCyan.copy(alpha = 0.7f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                if (isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(PrimaryPurple.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("▶", color = Color.White, fontSize = 18.sp)
                        }
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = track.title,
                    color = OnSurfaceDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
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
            .padding(horizontal = 20.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
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
                        .background(
                            Brush.linearGradient(listOf(PrimaryPurple, AccentCyan.copy(alpha = 0.7f)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(PrimaryPurple.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", color = Color.White, fontSize = 13.sp)
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
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = track.artist,
                color = OnSurfaceVariantDark,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onSaveToggle,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = if (isSaved) "♥" else "♡",
                color = if (isSaved) SecondaryPink else OnSurfaceVariantDark,
                fontSize = 18.sp
            )
        }
    }
}
