package com.novmusic.ui.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novmusic.BuildConfig
import com.novmusic.VkCallbackHolder
import com.novmusic.data.model.User
import com.novmusic.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
}

sealed class AuthEvent {
    object Idle : AuthEvent()
    object Loading : AuthEvent()
    data class Error(val message: String) : AuthEvent()
    data class Success(val message: String) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authEvent = MutableStateFlow<AuthEvent>(AuthEvent.Idle)
    val authEvent: StateFlow<AuthEvent> = _authEvent.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }

        viewModelScope.launch {
            VkCallbackHolder.vkTokenFlow.collect { (token, userId) ->
                signInWithVk(token, userId, "VK User")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authEvent.value = AuthEvent.Loading
            val result = authRepository.signInWithEmail(email, password)
            _authEvent.value = result.fold(
                onSuccess = { AuthEvent.Idle },
                onFailure = { AuthEvent.Error(mapFirebaseError(it.message)) }
            )
        }
    }

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _authEvent.value = AuthEvent.Loading
            val result = authRepository.registerWithEmail(email, password, displayName)
            _authEvent.value = result.fold(
                onSuccess = { AuthEvent.Idle },
                onFailure = { AuthEvent.Error(mapFirebaseError(it.message)) }
            )
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authEvent.value = AuthEvent.Loading
            val result = authRepository.sendPasswordReset(email)
            _authEvent.value = result.fold(
                onSuccess = { AuthEvent.Success("Письмо с инструкцией отправлено на $email") },
                onFailure = { AuthEvent.Error(mapFirebaseError(it.message)) }
            )
        }
    }

    fun openVkAuth(context: Context) {
        val appId = BuildConfig.VK_APP_ID
        val redirectUri = "novmusic://vk-callback"
        val scope = "audio,offline"
        val vkAuthUrl = "https://oauth.vk.com/authorize" +
                "?client_id=$appId" +
                "&redirect_uri=$redirectUri" +
                "&scope=$scope" +
                "&response_type=token" +
                "&v=5.131"

        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(vkAuthUrl))
    }

    private fun signInWithVk(token: String, userId: String, userName: String) {
        viewModelScope.launch {
            _authEvent.value = AuthEvent.Loading
            val result = authRepository.signInWithVkToken(token, userId, userName)
            _authEvent.value = result.fold(
                onSuccess = { AuthEvent.Idle },
                onFailure = { AuthEvent.Error("Ошибка входа через VK: ${it.message}") }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearEvent() {
        _authEvent.value = AuthEvent.Idle
    }

    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Неизвестная ошибка"
            message.contains("INVALID_EMAIL") || message.contains("badly formatted") -> "Неверный формат email"
            message.contains("WRONG_PASSWORD") || message.contains("invalid credential") -> "Неверный email или пароль"
            message.contains("USER_NOT_FOUND") -> "Пользователь не найден"
            message.contains("EMAIL_ALREADY_IN_USE") || message.contains("already in use") -> "Email уже используется"
            message.contains("WEAK_PASSWORD") || message.contains("at least 6") -> "Пароль должен быть не менее 6 символов"
            message.contains("NETWORK_ERROR") || message.contains("network") -> "Ошибка сети. Проверьте соединение"
            message.contains("TOO_MANY_REQUESTS") -> "Слишком много попыток. Подождите и попробуйте снова"
            else -> "Ошибка: $message"
        }
    }
}
