package com.novmusic.ui.viewmodel

  import androidx.lifecycle.ViewModel
  import androidx.lifecycle.viewModelScope
  import com.novmusic.data.model.Track
  import com.novmusic.data.player.PlayerController
  import com.novmusic.data.player.PlayerState
  import com.novmusic.data.repository.MusicRepository
  import dagger.hilt.android.lifecycle.HiltViewModel
  import kotlinx.coroutines.flow.MutableStateFlow
  import kotlinx.coroutines.flow.StateFlow
  import kotlinx.coroutines.flow.asStateFlow
  import kotlinx.coroutines.launch
  import javax.inject.Inject

  data class MusicUiState(
      val searchQuery: String = "",
      val searchResults: List<Track> = emptyList(),
      val trendingTracks: List<Track> = emptyList(),
      val savedTracks: List<Track> = emptyList(),
      val historyTracks: List<Track> = emptyList(),
      val isSearchLoading: Boolean = false,
      val isTrendingLoading: Boolean = false,
      val isSavedLoading: Boolean = false,
      val isHistoryLoading: Boolean = false,
      val trendingError: String? = null,
      val searchError: String? = null,
      val error: String? = null,
      val savedTrackIds: Set<String> = emptySet(),
      val saveMessage: String? = null
  )

  @HiltViewModel
  class MusicViewModel @Inject constructor(
      private val musicRepository: MusicRepository,
      val playerController: PlayerController
  ) : ViewModel() {

      private val _uiState = MutableStateFlow(MusicUiState())
      val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

      val playerState: StateFlow<PlayerState> = playerController.playerState

      init {
          loadTrendingTracks()
          loadSavedTracks()
          loadHistory()
      }

      fun onSearchQueryChange(query: String) {
          _uiState.value = _uiState.value.copy(searchQuery = query, searchError = null)
      }

      fun search() {
          val query = _uiState.value.searchQuery.trim()
          if (query.isBlank()) return
          viewModelScope.launch {
              _uiState.value = _uiState.value.copy(isSearchLoading = true, searchError = null)
              val result = musicRepository.searchTracks(query)
              if (result.isSuccess) {
                  _uiState.value = _uiState.value.copy(
                      isSearchLoading = false,
                      searchResults = result.getOrDefault(emptyList())
                  )
              } else {
                  val e = result.exceptionOrNull()
                  _uiState.value = _uiState.value.copy(
                      isSearchLoading = false,
                      searchResults = emptyList(),
                      searchError = e?.message ?: (e?.javaClass?.simpleName ?: "Неизвестная ошибка")
                  )
              }
          }
      }

      fun loadTrendingTracks() {
          viewModelScope.launch {
              _uiState.value = _uiState.value.copy(isTrendingLoading = true, trendingError = null)
              val result = musicRepository.getTrendingTracks()
              if (result.isSuccess) {
                  _uiState.value = _uiState.value.copy(
                      isTrendingLoading = false,
                      trendingTracks = result.getOrDefault(emptyList())
                  )
              } else {
                  val e = result.exceptionOrNull()
                  _uiState.value = _uiState.value.copy(
                      isTrendingLoading = false,
                      trendingTracks = emptyList(),
                      trendingError = e?.message ?: (e?.javaClass?.simpleName ?: "Неизвестная ошибка")
                  )
              }
          }
      }

      fun loadSavedTracks() {
          viewModelScope.launch {
              _uiState.value = _uiState.value.copy(isSavedLoading = true)
              musicRepository.getSavedTracks().collect { tracks ->
                  val ids = tracks.map { it.id }.toSet()
                  _uiState.value = _uiState.value.copy(
                      isSavedLoading = false,
                      savedTracks = tracks,
                      savedTrackIds = ids
                  )
              }
          }
      }

      fun loadHistory() {
          viewModelScope.launch {
              _uiState.value = _uiState.value.copy(isHistoryLoading = true)
              musicRepository.getHistory(20).collect { tracks ->
                  _uiState.value = _uiState.value.copy(
                      isHistoryLoading = false,
                      historyTracks = tracks
                  )
              }
          }
      }

      fun playTrack(track: Track) {
          playerController.playTrack(track)
          viewModelScope.launch { musicRepository.addToHistory(track) }
      }

      fun togglePlayPause() { playerController.togglePlayPause() }

      fun seekTo(position: Long) { playerController.seekTo(position) }

      fun toggleSaveTrack(track: Track) {
          viewModelScope.launch {
              if (_uiState.value.savedTrackIds.contains(track.id)) {
                  musicRepository.removeSavedTrack(track.id)
                  _uiState.value = _uiState.value.copy(saveMessage = "Удалено из сохранённых")
              } else {
                  musicRepository.saveTrack(track)
                  _uiState.value = _uiState.value.copy(saveMessage = "Сохранено")
              }
          }
      }

      fun clearSaveMessage() { _uiState.value = _uiState.value.copy(saveMessage = null) }

      fun clearError() {
          _uiState.value = _uiState.value.copy(error = null, trendingError = null, searchError = null)
      }

      fun isTrackSaved(trackId: String): Boolean = _uiState.value.savedTrackIds.contains(trackId)
  }
  