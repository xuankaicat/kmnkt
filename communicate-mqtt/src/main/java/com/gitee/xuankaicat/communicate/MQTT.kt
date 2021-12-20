@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap
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

    override var inMessageTopic: String = ""
    override var outMessageTopic: String = ""

    override var serverPort = 9000
    override var address: String = ""

    private val serverURI
        get() = "tcp://${address}:${serverPort}"

    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false

    private val onReceives = HashMap<String, OnReceiveFunc>(3)

    override fun send(message: String) = send(outMessageTopic, message)

    override fun send(topic: String, message: String) {
        thread {
            try {
                //参数分别为：主题、消息的字节数组、服务质量、是否在服务器保留断开连接后的最后一条消息
                client?.publish(topic, message.toByteArray(outCharset), _qos, retained)
                Log.v("MQTT", "发送消息 {uri: '${serverURI}', topic: '$topic', message: '${message}'}")
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    override fun addInMessageTopic(topic: String, onReceive: OnReceiveFunc) {
        if(client == null) return
        onReceives[topic] = onReceive
        client?.subscribe(topic, _qos)
    }

    override fun removeInMessageTopic(topic: String) {
        if(client == null) return
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
        var success = false
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
            val message = "{\"terminal_uid\":\"$clientId\"}"

            do {
                try {
                    options!!.apply {
                        try {
                            setWill(inMessageTopic, message.toByteArray(), _qos, retained)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            doConnect = false
                        }
                    }

                    if(doConnect) {
                        doClientConnection()
                        success = true
                        onOpenCallback.success(this)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if(!success) success = onOpenCallback.failure(this)
                }
            } while (!success)
        }
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
                    Thread.sleep(1000)
                }
            } while (!success && times <= 5)
            if(success) {
                Log.v("MQTT", "连接成功于第${times}次 {uri: '${serverURI}', username: '${username}', password: '${password}'}")
            }
        }
    }

    //订阅主题的回调
    private val mqttCallback: MqttCallback = object : MqttCallback {
        override fun messageArrived(topic: String, message: MqttMessage) {
            //收到消息 String(message.payload)
            val msg = String(message.payload, inCharset)
            Log.v("MQTT", "收到来自[${topic}]的消息\"${msg}\"")
            Log.v("MQTT", "开始获取notstop")
            var notStop = true
            Handler(Looper.getMainLooper()).post {
                notStop = this@MQTT.onReceives[topic]?.invoke(msg, topic) == false
            }
            Log.v("MQTT", "notstop的值为$notStop")
            if(!notStop) {
                Log.v("MQTT", "关闭监听")
                removeInMessageTopic(topic)
            } else {
                Log.v("MQTT", "继续监听")
            }
        }

        override fun deliveryComplete(arg0: IMqttDeliveryToken) {}
        override fun connectionLost(arg0: Throwable) {
            doClientConnection() //连接断开，重连
        }
    }
}