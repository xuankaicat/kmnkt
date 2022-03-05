package com.gitee.xuankaicat.kmnkt.socket.utils

import android.os.Handler
import android.os.Looper

actual fun mainThread(block: () -> Unit) {
    Handler(Looper.getMainLooper()).post(block)
}