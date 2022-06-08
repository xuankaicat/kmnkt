package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Charset
import com.gitee.xuankaicat.kmnkt.socket.utils.Thread

actual open class TCP : ISocket {
    override val socket: Any?
        get() = TODO("Not yet implemented")
    override var port: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var address: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var callbackOnMain: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var inCharset: Charset
        get() = TODO("Not yet implemented")
        set(value) {}
    override var outCharset: Charset
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun send(message: String) {
        TODO("Not yet implemented")
    }

    override fun send(message: String, times: Int, delay: Long): Thread {
        TODO("Not yet implemented")
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        TODO("Not yet implemented")
    }

    override fun stopReceive() {
        TODO("Not yet implemented")
    }

    override fun open(onOpenCallback: IOnOpenCallback) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }
}