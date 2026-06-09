package com.novmusic

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object VkCallbackHolder {
    private val _vkTokenFlow = MutableSharedFlow<Triple<String, String, String>>(extraBufferCapacity = 1)
    val vkTokenFlow: SharedFlow<Triple<String, String, String>> = _vkTokenFlow.asSharedFlow()

    fun onVkCallback(token: String, userId: String, userName: String = "VK User") {
        _vkTokenFlow.tryEmit(Triple(token, userId, userName))
    }
}
