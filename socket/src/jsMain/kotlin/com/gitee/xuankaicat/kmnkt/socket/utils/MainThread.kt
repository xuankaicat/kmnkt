package com.gitee.xuankaicat.kmnkt.socket.utils

actual fun mainThread(block: () -> Unit) = block()