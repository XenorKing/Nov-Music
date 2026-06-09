package com.novmusic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.novmusic.data.model.AuthProvider
import com.novmusic.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    val currentFirebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toUser() ?: return Result.failure(Exception("Пользователь не найден"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
            )?.await()
            val user = result.user?.toUser() ?: return Result.failure(Exception("Ошибка создания пользователя"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithVkToken(vkToken: String, vkUserId: String, userName: String): Result<User> {
        return try {
            // При VK OAuth используем кастомный токен через Firebase Custom Auth
            // Для простоты реализации создаём/обновляем пользователя через анонимный вход
            // с последующей привязкой VK данных в Firestore
            // В продакшене нужен Firebase Custom Token от бэкенда
            val email = "vk_$vkUserId@novmusic.vk"
            val password = "vk_${vkToken.take(16)}"

            val result = try {
                firebaseAuth.signInWithEmailAndPassword(email, password).await()
            } catch (e: Exception) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            }

            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()
            )?.await()

            val user = result.user?.toUser(AuthProvider.VK)
                ?: return Result.failure(Exception("Ошибка входа через VK"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUser(provider: AuthProvider = AuthProvider.EMAIL) = User(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        authProvider = provider
    )
}
