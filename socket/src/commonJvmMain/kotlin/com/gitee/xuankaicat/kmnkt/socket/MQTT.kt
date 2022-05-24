@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Log
import com.gitee.xuankaicat.kmnkt.socket.utils.mainThread
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.thread

open class MQTT : IMqttSocket {
    private var client: MqttClient? = null
    override val socket: Any?
        get() = client

    private var _qos = 2
    override var qos: MqttQuality
        get() = MqttQuality.values()[_qos]
        set(value) {
            _qos = value.ordinal
        }

    override var timeOut: Int = 10
    override var cleanSession: Boolean = true
    override var keepAliveInterval: Int = 20

    private val retained = false
    override var options: MqttConnectOptions? = null

    override var username: String = ""
    override var password: String = ""
    override var clientId: String = ""

    override var inMessageTopic: String = ""
    override var outMessageTopic: String = ""

    override var uriType: String = "tcp"
    override var port: Int = 9000
    override var address: String = "10.0.2.2"
    override var path: String = ""

    private val serverURI
        get() = "${uriType}://${address}:${port}${path}"

    override var callbackOnMain: Boolean = true

    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false

    private val onReceives = HashMap<String, OnReceiveFunc>(3)
    private var onOpenCallback: IOnOpenCallback = OnOpenCallback()

    override fun send(message: String) = send(outMessageTopic, message)

    override fun send(topic: String, message: String) {
        thread {
            sendSync(topic, message)
        }
    }

    override fun sendSync(message: String) = sendSync(outMessageTopic, message)

    override fun sendSync(topic: String, message: String) {
        try {
            //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
            client?.publish(topic, message.toByteArray(outCharset), _qos, retained)
            Log.v("MQTT", "发送消息 {uri: '${serverURI}', topic: '$topic', message: '${message}'}")
        } catch (e: MqttException) {
            Log.e("MQTT", "发送消息失败 {uri: '${serverURI}', topic: '$topic', message: '${message}'}")
            e.printStackTrace()
        }
    }

    override fun send(topic: String, message: String, times: Int, delay: Long): Thread = thread {
        var nowTimes = times
        Log.v("MQTT", "开始循环发送信息,剩余次数: $nowTimes, 间隔: $delay {uri: '${address}', port: ${port}}")
        while (nowTimes != 0) {
            send(message)
            Thread.sleep(delay)
            if(nowTimes > 0) nowTimes--
        }
    }

    override fun send(message: String, times: Int, delay: Long): Thread = thread {
        var nowTimes = times
        Log.v("MQTT", "开始循环发送信息,剩余次数: $nowTimes, 间隔: $delay {uri: '${address}', port: ${port}}")
        while (nowTimes != 0) {
            send(message)
            Thread.sleep(delay)
            if(nowTimes > 0) nowTimes--
        }
    }

    override fun sendAndReceive(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        addInMessageTopic(inTopic, onReceive)
        send(outTopic, message)
    }

    override fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        addInMessageTopic(inTopic, onReceive)
        sendSync(outTopic, message)
    }

    override fun addInMessageTopic(topic: String, onReceive: OnReceiveFunc) {
        if(client == null) return
        Log.v("MQTT", "开始订阅$topic")
        onReceives[topic] = onReceive
        client?.subscribe(topic, _qos)
    }

    override fun removeInMessageTopic(topic: String) {
        if(client == null) return
        Log.v("MQTT", "结束订阅$topic")
        onReceives.remove(topic)
        client?.unsubscribe(topic)
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(client == null) return false
        if(isReceiving) return false
        isReceiving = true
        onReceives[inMessageTopic] = onReceive
        client?.subscribe(inMessageTopic, _qos)//订阅主题，参数：主题、服务质量
        return true
    }

    override fun stopReceive() {
        if(!isReceiving) return
        onReceives.remove(inMessageTopic)
        isReceiving = false
        client?.unsubscribe(inMessageTopic)
    }

    override fun open(onOpenCallback: IOnOpenCallback) {
        onReceives.clear()
        //存储回调对象
        this.onOpenCallback = onOpenCallback
        //初始化连接对象
        thread {
            val tmpDir = System.getProperty("java.io.tmpdir")
            val dataStore = MqttDefaultFilePersistence(tmpDir)

            if(clientId == "") clientId = UUID.randomUUID().toString()
            client = MqttClient(serverURI, clientId, dataStore)
            client?.setCallback(mqttCallback)

            var doConnect = true
            if(options == null) {
                options = MqttConnectOptions()
            }
            options?.apply {
                connectionTimeout = timeOut
                isCleanSession = cleanSession
                keepAliveInterval = this.keepAliveInterval
                userName = this@MQTT.username
                password = this@MQTT.password.toCharArray()
            }
            if(inMessageTopic.isNotEmpty()) {
                //设置LWT
                val message = "{\"terminal_uid\":\"$clientId\"}"
                try {
                    options!!.apply {
                        try {
                            setWill(inMessageTopic, message.toByteArray(), _qos, retained)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            doConnect = false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //执行连接
            if(doConnect) doClientConnection()
        }
    }

    override fun close() {
        client?.disconnect()
    }

    private fun doClientConnection() {
        if(client?.isConnected == false) {
            var success: Boolean
            do {
                try {
                    Log.v("MQTT", "开始进行连接 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                    client?.connect(options) //如果连接失败会在这里抛出异常
                    success = true
                    Log.v("MQTT", "连接成功 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                    onOpenCallback.success(this)
                } catch (e: Exception) {
                    Log.e("MQTT", "连接失败 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                    e.printStackTrace()
                    //失败回调
                    success = !onOpenCallback.failure(this)
                    if(success) {
                        Log.v("MQTT", "回调返回false,放弃连接 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                    }
                }
            } while (!success)
        }
    }

    //订阅主题的回调
    private val mqttCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String, message: MqttMessage) {
            //收到消息 String(message.payload)
            val msg = String(message.payload, inCharset)
            Log.v("MQTT", "收到来自[${topic}]的消息\"${msg}\"")
            var notStop = true
            if (callbackOnMain) {
                mainThread {
                    notStop = this@MQTT.onReceives[topic]?.invoke(msg, topic) == false
                }
            } else {
                notStop = this@MQTT.onReceives[topic]?.invoke(msg, topic) == false
            }
            if(!notStop) {
                removeInMessageTopic(topic)
            }
        }

        override fun deliveryComplete(arg0: IMqttDeliveryToken) {}
        override fun connectionLost(arg0: Throwable) {
            if(onOpenCallback.loss(this@MQTT)) {
                //重新连接
                doClientConnection()
                if(cleanSession) {
                    stopReceive()
                    Log.w("MQTT", "重连成功，消息可能需要重新订阅。如果不希望在重连后重新订阅，可以设置连接对象的cleanSession字段为false。 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                }
            }
        }
    }
}