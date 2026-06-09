package com.novmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.novmusic.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val nickname: String = "",
    val successMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(isLoggedIn = authRepository.isLoggedIn)
        if (authRepository.isLoggedIn) {
            loadNickname()
        }
    }

    fun loadNickname() {
        viewModelScope.launch {
            val nickname = authRepository.getNickname()
            _uiState.value = _uiState.value.copy(nickname = nickname)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email.trim(), password)
            if (result.isSuccess) {
                loadNickname()
                _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = mapFirebaseError(result.exceptionOrNull()?.message)
                )
            }
        }
    }

    fun register(email: String, password: String, nickname: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.register(email.trim(), password, nickname.trim())
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoggedIn = true,
                    nickname = nickname.trim()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = mapFirebaseError(result.exceptionOrNull()?.message)
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.sendPasswordReset(email.trim())
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, successMessage = "Письмо отправлено на $email")
            } else {
                _uiState.value.copy(isLoading = false, error = mapFirebaseError(result.exceptionOrNull()?.message))
            }
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.updateNickname(nickname.trim())
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, nickname = nickname.trim(), successMessage = "Никнейм обновлён")
            } else {
                _uiState.value.copy(isLoading = false, error = mapFirebaseError(result.exceptionOrNull()?.message))
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.updatePassword(newPassword)
            _uiState.value = if (result.isSuccess) {
                _uiState.value.copy(isLoading = false, successMessage = "Пароль изменён")
            } else {
                _uiState.value.copy(isLoading = false, error = mapFirebaseError(result.exceptionOrNull()?.message))
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState(isLoggedIn = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Неизвестная ошибка"
            message.contains("INVALID_EMAIL") || message.contains("invalid-email") -> "Неверный формат email"
            message.contains("WRONG_PASSWORD") || message.contains("wrong-password") -> "Неверный пароль"
            message.contains("USER_NOT_FOUND") || message.contains("user-not-found") -> "Пользователь не найден"
            message.contains("EMAIL_ALREADY_IN_USE") || message.contains("email-already-in-use") -> "Email уже зарегистрирован"
            message.contains("WEAK_PASSWORD") || message.contains("weak-password") -> "Пароль слишком простой (минимум 6 символов)"
            message.contains("NETWORK_ERROR") || message.contains("network") -> "Проблема с интернетом"
            message.contains("TOO_MANY_REQUESTS") || message.contains("too-many-requests") -> "Слишком много попыток. Попробуйте позже"
            else -> "Ошибка: $message"
        }
    }
}
