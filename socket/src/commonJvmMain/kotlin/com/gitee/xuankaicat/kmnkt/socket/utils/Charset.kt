package com.gitee.xuankaicat.kmnkt.socket.utils

import com.gitee.xuankaicat.kmnkt.socket.ISocket

actual typealias Charset = java.nio.charset.Charset

class CharsetUtils {
    companion object {
        @JvmStatic
        fun ISocket.setInCharset(charset: Charset) {
            this.inCharset = charset
        }

        @JvmStatic
        fun ISocket.setOutCharset(charset: Charset) {
            this.outCharset = charset
        }
    }

}

