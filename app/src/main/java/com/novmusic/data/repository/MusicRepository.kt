package com.novmusic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.novmusic.data.api.JamendoMusicApi
import com.novmusic.data.model.SavedTrack
import com.novmusic.data.model.Track
import com.novmusic.data.model.toSavedTrack
import com.novmusic.data.model.toTrack
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepository @Inject constructor(
    private val jamendoApi: JamendoMusicApi,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun searchTracks(query: String, offset: Int = 0): Result<List<Track>> {
        return try {
            val response = jamendoApi.searchTracks(search = query, offset = offset)
            val tracks = response.results
                .filter { !it.audio.isNullOrBlank() || !it.audiodownload.isNullOrBlank() }
                .map { it.toTrack() }
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingTracks(): Result<List<Track>> {
        return try {
            val response = jamendoApi.getTrendingTracks()
            val tracks = response.results
                .filter { !it.audio.isNullOrBlank() || !it.audiodownload.isNullOrBlank() }
                .map { it.toTrack() }
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSavedTracks(): Flow<List<Track>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users")
            .document(uid)
            .collection("saved_tracks")
            .orderBy("savedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val tracks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(SavedTrack::class.java)?.toTrack()
                } ?: emptyList()
                trySend(tracks)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveTrack(track: Track): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Необходима авторизация"))
            firestore.collection("users").document(uid)
                .collection("saved_tracks").document(track.id)
                .set(track.toSavedTrack()).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun removeSavedTrack(trackId: String): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Необходима авторизация"))
            firestore.collection("users").document(uid)
                .collection("saved_tracks").document(trackId)
                .delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun isTrackSaved(trackId: String): Boolean {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return false
            val doc = firestore.collection("users").document(uid)
                .collection("saved_tracks").document(trackId).get().await()
            doc.exists()
        } catch (e: Exception) { false }
    }

    // ---- History ----

    suspend fun addToHistory(track: Track) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        try {
            val historyEntry = mapOf(
                "id" to track.id,
                "title" to track.title,
                "artist" to track.artist,
                "artworkUrl" to (track.artworkUrl ?: ""),
                "streamUrl" to (track.streamUrl ?: ""),
                "duration" to track.duration,
                "playedAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid)
                .collection("history").document(track.id)
                .set(historyEntry).await()
        } catch (_: Exception) {}
    }

    fun getHistory(limit: Int = 20): Flow<List<Track>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("users")
            .document(uid)
            .collection("history")
            .orderBy("playedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) { trySend(emptyList()); return@addSnapshotListener }
                val tracks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Track(
                            id = doc.getString("id") ?: return@mapNotNull null,
                            title = doc.getString("title") ?: "",
                            artist = doc.getString("artist") ?: "",
                            artworkUrl = doc.getString("artworkUrl")?.ifBlank { null },
                            streamUrl = doc.getString("streamUrl")?.ifBlank { null },
                            duration = doc.getLong("duration") ?: 0L,
                            permalinkUrl = "",
                            genre = null,
                            playbackCount = 0,
                            likesCount = 0
                        )
                    } catch (_: Exception) { null }
                } ?: emptyList()
                trySend(tracks)
            }
        awaitClose { listener.remove() }
    }
}
