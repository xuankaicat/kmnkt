package com.gitee.xuankaicat.kmnkt.socket.utils

import kotlinx.coroutines.*

actual fun mainThread(block: () -> Unit) {
    runBlocking(Dispatchers.Main) {
        block()
    }
}