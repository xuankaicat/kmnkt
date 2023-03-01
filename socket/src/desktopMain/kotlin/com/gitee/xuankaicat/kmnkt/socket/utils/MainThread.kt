package com.gitee.xuankaicat.kmnkt.socket.utils

import kotlinx.coroutines.*

actual fun mainThread(block: suspend () -> Unit) {
    runBlocking(Dispatchers.Main) {
        block()
    }
}