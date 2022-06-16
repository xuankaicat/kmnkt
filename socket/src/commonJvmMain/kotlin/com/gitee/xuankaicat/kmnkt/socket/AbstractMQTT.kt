@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import java.nio.charset.Charset

abstract class AbstractMQTT : IMqttSocket {
    override var enableDefaultLog = true

    protected var _qos = 2
    override var qos: MqttQuality
        get() = MqttQuality.values()[_qos]
        set(value) {
            _qos = value.ordinal
        }

    override var timeOut: Int = 10
    override var cleanSession: Boolean = true
    override var keepAliveInterval: Int = 20

    protected val retained = false

    override var username: String = ""
    override var password: String = ""
    override var clientId: String = ""

    override var inMessageTopic: String = ""
    override var outMessageTopic: String = ""

    override var uriType: String = "tcp"
    override var port: Int = 9000
    override var address: String = "10.0.2.2"
    override var path: String = ""

    protected val serverURI
        get() = "${uriType}://${address}:${port}${path}"

    override var callbackOnMain: Boolean = true

    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    override var threadLock: Boolean = false
}