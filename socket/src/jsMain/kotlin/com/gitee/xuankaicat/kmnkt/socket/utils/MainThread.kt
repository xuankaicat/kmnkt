package com.gitee.xuankaicat.kmnkt.socket.utils

import kotlinx.coroutines.*

actual class Thread

actual fun mainThread(block: () -> Unit) {
    MainScope().promise {
        block()
    }
}