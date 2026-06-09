package com.novmusic.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUser: FirebaseUser? get() = firebaseAuth.currentUser
    val isLoggedIn: Boolean get() = firebaseAuth.currentUser != null

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, nickname: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            // Save nickname to Firestore
            firestore.collection("users").document(user.uid)
                .set(mapOf(
                    "nickname" to nickname,
                    "email" to email,
                    "createdAt" to System.currentTimeMillis()
                )).await()
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

    suspend fun getNickname(): String {
        val uid = firebaseAuth.currentUser?.uid ?: return ""
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            doc.getString("nickname") ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun updateNickname(nickname: String): Result<Unit> {
        val uid = firebaseAuth.currentUser?.uid
            ?: return Result.failure(Exception("Не авторизован"))
        return try {
            firestore.collection("users").document(uid)
                .update("nickname", nickname).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Не авторизован"))
        return try {
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
