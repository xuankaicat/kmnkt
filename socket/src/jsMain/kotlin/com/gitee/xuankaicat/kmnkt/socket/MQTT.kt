package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Charset
import com.gitee.xuankaicat.kmnkt.socket.utils.MqttConnectOptions
import com.gitee.xuankaicat.kmnkt.socket.utils.Thread

actual open class MQTT : IMqttSocket {
    override var enableDefaultLog = true
    override var qos: MqttQuality
        get() = TODO("Not yet implemented")
        set(value) {}
    override var uriType: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var path: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var timeOut: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var cleanSession: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var keepAliveInterval: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var username: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var password: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var clientId: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var inMessageTopic: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var outMessageTopic: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var options: MqttConnectOptions?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var threadLock: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun addInMessageTopic(topic: String, onReceive: OnReceiveFunc) {
        TODO("Not yet implemented")
    }

    override fun removeInMessageTopic(topic: String) {
        TODO("Not yet implemented")
    }

    override fun sendSync(message: String) {
        TODO("Not yet implemented")
    }

    override fun sendSync(topic: String, message: String) {
        TODO("Not yet implemented")
    }

    override fun send(topic: String, message: String) {
        TODO("Not yet implemented")
    }

    override fun send(topic: String, message: String, times: Int, delay: Long): Thread {
        TODO("Not yet implemented")
    }

    override fun send(message: String) {
        TODO("Not yet implemented")
    }

    override fun send(message: String, times: Int, delay: Long): Thread {
        TODO("Not yet implemented")
    }

    override fun sendAndReceive(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        TODO("Not yet implemented")
    }

    override fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        TODO("Not yet implemented")
    }

    override fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, timeout: Long): String? {
        TODO("Not yet implemented")
    }

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