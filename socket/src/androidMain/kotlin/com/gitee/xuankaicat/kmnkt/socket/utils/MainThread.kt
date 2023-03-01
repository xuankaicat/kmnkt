package com.gitee.xuankaicat.kmnkt.socket.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.runBlocking

actual fun mainThread(block: suspend () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        runBlocking { block() }
    }
}