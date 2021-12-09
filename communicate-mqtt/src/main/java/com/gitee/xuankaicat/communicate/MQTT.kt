@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import java.nio.charset.Charset
import java.util.*
import kotlin.concurrent.thread

class MQTT : MQTTCommunicate {
    private var client: MqttClient? = null
    private var _qos = 2
    override var qos: MqttQuality
        get() = MqttQuality.values()[_qos]
        set(value) {
            _qos = value.ordinal
        }

    private val retained = false
    override var options: MqttConnectOptions? = null

    override var username: String = ""
    override var password: String = ""
    override var clientId: String = ""

    override var publishTopic: String = ""
    override var responseTopic: String = ""

    override var serverPort = 9000
    override var address: String = ""

    private val serverURI
        get() = "tcp://${address}:${serverPort}"

    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false
    private var onReceive: OnReceiveFunc = {false}

    override fun send(message: String) {
        thread {
            try {
                //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
                client?.publish(responseTopic, message.toByteArray(outCharset), _qos, retained)
                Log.v("MQTT", "发送消息 {uri: '${serverURI}', topic: '${responseTopic}', message: '${message}'}")
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(client == null) return false
        if(isReceiving) return false
        isReceiving = true
        this.onReceive = onReceive
        client?.subscribe(publishTopic, _qos)//订阅主题，参数：主题、服务质量
        return true
    }

    override fun stopReceive() {
        if(!isReceiving) return
        this.onReceive = {false}
        isReceiving = false
        client?.unsubscribe(publishTopic)
    }

    override fun open(): Boolean {
        try {
            thread {
                val tmpDir = System.getProperty("java.io.tmpdir")
                val dataStore = MqttDefaultFilePersistence(tmpDir)

                if(clientId == "") clientId = UUID.randomUUID().toString()
                client = MqttClient(serverURI, clientId, dataStore)
                client?.setCallback(mqttCallback)

                var doConnect = true
                if(options == null) options = MqttConnectOptions()
                else {
                    options!!.apply {
                        isCleanSession = true
                        connectionTimeout = 10
                        keepAliveInterval = 20
                        userName = this@MQTT.username
                        password = this@MQTT.password.toCharArray()
                    }
                }
                options!!.apply {
                    val message = "{\"terminal_uid\":\"$clientId\"}"
                    try {
                        setWill(publishTopic, message.toByteArray(), _qos, retained)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        doConnect = false
                    }
                }

                if(doConnect) {
                    doClientConnection()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun close() {
        client?.disconnect()
    }

    private fun doClientConnection() {
        if(client?.isConnected == false) {
            var success = true
            var times = 1
            do {
                try {
                    client?.connect(options)
                } catch (e: Exception) {
                    Log.e("MQTT", "第${times}次连接失败 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
                    e.printStackTrace()
                    success = false
                    times++
                }
            } while (!success && times <= 20)
            if(success) {
                Log.v("MQTT", "连接成功于第${times}次 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
            }
        }
    }

    //订阅主题的回调
    private val mqttCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String, message: MqttMessage) {
            //收到消息 String(message.payload)
            if(!this@MQTT.onReceive.invoke(String(message.payload, inCharset))) {
                stopReceive()
            }
        }

        override fun deliveryComplete(arg0: IMqttDeliveryToken) {}
        override fun connectionLost(arg0: Throwable) {
            doClientConnection() //连接断开，重连
        }
    }
}