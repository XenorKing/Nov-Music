package com.novmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novmusic.ui.components.MiniPlayer
import com.novmusic.ui.theme.*
import com.novmusic.ui.viewmodel.MusicViewModel

@Composable
fun SearchScreen(musicViewModel: MusicViewModel) {
    val uiState by musicViewModel.uiState.collectAsState()
    val playerState by musicViewModel.playerState.collectAsState()
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Поиск",
                color = OnSurfaceDark,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { musicViewModel.onSearchQueryChange(it) },
                placeholder = { Text("Трек, исполнитель...", color = OnSurfaceVariantDark) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = PrimaryPurple) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(onClick = { musicViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, null, tint = OnSurfaceVariantDark)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    focusManager.clearFocus()
                    musicViewModel.search()
                }),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = Color(0xFF4A4A6A),
                    focusedTextColor = OnSurfaceDark,
                    unfocusedTextColor = OnSurfaceDark,
                    cursorColor = PrimaryPurple,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isSearchLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryPurple)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Ищем треки...", color = OnSurfaceVariantDark)
                    }
                }
            } else if (uiState.searchResults.isEmpty() && uiState.searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            tint = OnSurfaceVariantDark,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ничего не найдено", color = OnSurfaceVariantDark, fontSize = 16.sp)
                    }
                }
            } else if (uiState.searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            tint = OnSurfaceVariantDark,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Начните поиск", color = OnSurfaceVariantDark, fontSize = 16.sp)
                        Text(
                            "Введите название трека или исполнителя",
                            color = OnSurfaceVariantDark.copy(alpha = 0.6f),
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
                            text = "Найдено: ${uiState.searchResults.size} треков",
                            color = OnSurfaceVariantDark,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                        )
                    }
                    items(uiState.searchResults) { track ->
                        TrackListItem(
                            track = track,
                            isSaved = uiState.savedTrackIds.contains(track.id),
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
    }
}
