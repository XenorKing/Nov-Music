package com.novmusic

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object VkCallbackHolder {
    private val _vkTokenFlow = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 1)
    val vkTokenFlow: SharedFlow<Pair<String, String>> = _vkTokenFlow.asSharedFlow()

    fun onVkCallback(token: String, userId: String) {
        _vkTokenFlow.tryEmit(Pair(token, userId))
    }
}
