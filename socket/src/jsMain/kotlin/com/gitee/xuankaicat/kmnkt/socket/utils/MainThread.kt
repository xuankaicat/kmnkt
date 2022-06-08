package com.gitee.xuankaicat.kmnkt.socket.utils

import kotlinx.coroutines.*

actual fun mainThread(block: () -> Unit) {
    MainScope().promise {
        block()
    }
}