package com.novmusic.data.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val authProvider: AuthProvider
)

enum class AuthProvider {
    EMAIL,
    VK
}
