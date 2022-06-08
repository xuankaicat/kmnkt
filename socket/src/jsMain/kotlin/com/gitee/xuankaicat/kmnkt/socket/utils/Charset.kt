@file:Suppress("unused", "unused_parameter")
package com.gitee.xuankaicat.kmnkt.socket.utils

actual abstract class Charset

enum class Charsets(value: String) {
    GB2312("gb2312"),
    UTF_8("utf-8"),
}