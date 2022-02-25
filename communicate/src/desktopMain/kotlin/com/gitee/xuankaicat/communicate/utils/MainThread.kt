package com.gitee.xuankaicat.communicate.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * 使用协程尝试实现跳回主线程效果，待测试
 */
actual fun mainThread(block: () -> Unit) {
    runBlocking(Dispatchers.Main) {
        block()
    }
}