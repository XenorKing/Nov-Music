package com.novmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novmusic.ui.components.MiniPlayer
import com.novmusic.ui.theme.*
import com.novmusic.ui.viewmodel.MusicViewModel

@Composable
fun SavedTracksScreen(musicViewModel: MusicViewModel) {
    val uiState by musicViewModel.uiState.collectAsState()
    val playerState by musicViewModel.playerState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Bookmark, null, tint = PrimaryPurple, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Избранное",
                    color = OnSurfaceDark,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.isSavedLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPurple)
                }
            } else if (uiState.savedTracks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            tint = OnSurfaceVariantDark,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Нет сохранённых треков",
                            color = OnSurfaceDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Нажмите ♡ рядом с треком, чтобы сохранить его",
                            color = OnSurfaceVariantDark,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        bottom = if (playerState.currentTrack != null) 80.dp else 16.dp
                    )
                ) {
                    item {
                        Text(
                            text = "${uiState.savedTracks.size} треков",
                            color = OnSurfaceVariantDark,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    }
                    items(uiState.savedTracks) { track ->
                        TrackListItem(
                            track = track,
                            isSaved = true,
                            isPlaying = playerState.currentTrack?.id == track.id && playerState.isPlaying,
                            onClick = { musicViewModel.playTrack(track) },
                            onSaveToggle = { musicViewModel.toggleSaveTrack(track) }
                        )
                    }
                }
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
