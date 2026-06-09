package com.novmusic.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VkTokenStorage @Inject constructor() {

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    fun saveToken(token: String) {
        _token.value = token
    }

    fun getToken(): String? = _token.value

    fun clear() {
        _token.value = null
    }
}
