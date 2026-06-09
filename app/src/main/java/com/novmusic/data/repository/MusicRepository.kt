package com.novmusic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.novmusic.data.VkTokenStorage
import com.novmusic.data.api.VkMusicApi
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
    private val vkMusicApi: VkMusicApi,
    private val vkTokenStorage: VkTokenStorage,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private fun requireToken(): Result<String> {
        val token = vkTokenStorage.getToken()
        return if (token != null) Result.success(token)
        else Result.failure(Exception("Войдите через ВКонтакте для доступа к музыке"))
    }

    suspend fun searchTracks(query: String, offset: Int = 0): Result<List<Track>> {
        return try {
            val tokenResult = requireToken()
            if (tokenResult.isFailure) return Result.failure(tokenResult.exceptionOrNull()!!)
            val token = tokenResult.getOrThrow()

            val response = vkMusicApi.searchTracks(query = query, token = token, offset = offset)

            if (response.error != null) {
                return Result.failure(Exception("VK ошибка ${response.error.errorCode}: ${response.error.errorMsg}"))
            }

            val tracks = response.response?.items
                ?.filter { it.url.isNotBlank() }
                ?.map { it.toTrack() }
                ?: emptyList()

            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingTracks(): Result<List<Track>> {
        return try {
            val tokenResult = requireToken()
            if (tokenResult.isFailure) return Result.failure(tokenResult.exceptionOrNull()!!)
            val token = tokenResult.getOrThrow()

            val response = vkMusicApi.getPopularTracks(token = token)

            val tracks = response.response
                .filter { it.url.isNotBlank() }
                .map { it.toTrack() }

            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserTracks(): Result<List<Track>> {
        return try {
            val tokenResult = requireToken()
            if (tokenResult.isFailure) return Result.failure(tokenResult.exceptionOrNull()!!)
            val token = tokenResult.getOrThrow()

            val response = vkMusicApi.getUserTracks(token = token)

            val tracks = response.response?.items
                ?.filter { it.url.isNotBlank() }
                ?.map { it.toTrack() }
                ?: emptyList()

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
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
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
            firestore.collection("users")
                .document(uid)
                .collection("saved_tracks")
                .document(track.id)
                .set(track.toSavedTrack())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeSavedTrack(trackId: String): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("Необходима авторизация"))
            firestore.collection("users")
                .document(uid)
                .collection("saved_tracks")
                .document(trackId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isTrackSaved(trackId: String): Boolean {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return false
            val doc = firestore.collection("users")
                .document(uid)
                .collection("saved_tracks")
                .document(trackId)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}
