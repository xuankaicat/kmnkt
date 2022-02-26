package com.gitee.xuankaicat.communicate.utils

import kotlinx.coroutines.*

actual fun mainThread(block: () -> Unit) {
    runBlocking(Dispatchers.Main) {
        block()
    }
}