@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.mainThread
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import kotlin.concurrent.thread

actual open class MQTT : AbstractMQTT(), IMqttSocket {
    private var client: MqttClient? = null
    override val socket: Any?
        get() = client

    override var options: MqttConnectOptions? = null

    private var receiving = -1
    private val onReceives = HashMap<String, MutableList<OnReceiveFunc?>?>(3)
    private var onOpenCallback: IOnOpenCallback = OnOpenCallback(this)

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
        Log.v("MQTT", "开始循环发送信息,次数: $nowTimes, 间隔: $delay {uri: '${address}', port: ${port}}")
        while (nowTimes != 0) {
            send(topic, message)
            Thread.sleep(delay)
            if(nowTimes > 0) nowTimes--
        }
    }

    override fun send(message: String, times: Int, delay: Long): Thread = send(outMessageTopic, message, times, delay)

    override fun sendAndReceive(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        addInMessageTopic(inTopic, onReceive)
        send(outTopic, message)
    }

    override fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc) {
        addInMessageTopic(inTopic, onReceive)
        sendSync(outTopic, message)
    }

    override fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, timeout: Long): String? {
        var result: String? = null

        val callback: OnReceiveFunc = { str, _ ->
            result = str
            false
        }

        sendAndReceiveSync(outTopic, inTopic, message, callback)

        if(timeout == -1L) {
            while (result == null) {
                Thread.sleep(2L)
            }
        } else {
            var nowTime = 0L
            while (result == null && nowTime < timeout) {
                Thread.sleep(2L)
                nowTime += 2L
            }

            if(nowTime >= timeout) {
                onReceives[inTopic]?.let {
                    it.removeIf { c -> c == callback }
                }
                removeInMessageTopic(inTopic)
            }
        }

        return result
    }

    private val paramPattern = "/[+#]".toRegex()
    private val expectPattern = "/([^/]+)"

    private fun String.processWildcard() = paramPattern.replace(this, expectPattern)

    override fun addInMessageTopic(topic: String, onReceive: OnReceiveFunc) {
        if(client == null) return
        lock(topic) {
            Log.v("MQTT", "开始订阅$topic")
            val fakeTopic = topic.processWildcard()
            onReceives[fakeTopic]?.let {
                it += onReceive
            } ?: run {
                onReceives[fakeTopic] = mutableListOf(onReceive)
                client?.subscribe(topic, _qos)
            }
        }
    }

    override fun removeInMessageTopic(topic: String) {
        if(client == null) return
        lock(topic) {
            val fakeTopic = topic.processWildcard()
            onReceives[fakeTopic]?.let {

                val mainTopic = if(topic == inMessageTopic) it[receiving] else null

                it.removeIf { callback -> callback == null }
                if(it.size == 0) {
                    onReceives.remove(fakeTopic)
                    client?.unsubscribe(topic)
                    Log.v("MQTT", "结束订阅$topic")
                } else if(mainTopic != null) {
                    receiving = it.indexOf(mainTopic)
                }
            }
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(client == null) return false
        if(receiving != -1) return false
        addInMessageTopic(inMessageTopic, onReceive)
        val fakeTopic = inMessageTopic.processWildcard()
        onReceives[fakeTopic]?.let {
            receiving = it.size - 1
        }
        return true
    }

    override fun stopReceive() {
        if(receiving == -1) return
        val fakeTopic = inMessageTopic.processWildcard()
        onReceives[fakeTopic]?.let {
            it[receiving] = null
        }
        removeInMessageTopic(inMessageTopic)
        receiving = -1
    }

    override fun open(onOpenCallback: IOnOpenCallback) {
        onReceives.clear()
        //存储回调对象
        this.onOpenCallback = onOpenCallback
        //初始化连接对象
        thread {
            val tmpDir = System.getProperty("java.io.tmpdir")
            val dataStore = MqttDefaultFilePersistence(tmpDir)

            if(clientId == "") clientId = java.util.UUID.randomUUID().toString()
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
            var doClean = false
            lock(topic) {
                onReceives.keys
                    .find { it.toRegex().containsMatchIn(topic) }
                    ?.let {
                        this@MQTT.onReceives[it]?.let { callbacks ->
                            //收到消息 String(message.payload)
                            val msg = String(message.payload, inCharset)
                            Log.v("MQTT", "收到来自[${topic}]的消息\"${msg}\"")

                            val callbackBlock = {
                                for (i in 0 until callbacks.size) {
                                    val stillRun = callbacks[i]?.let { it(msg, topic) } ?: false
                                    if(!stillRun) {
                                        callbacks[i] = null
                                        doClean = true
                                    }
                                }
                            }

                            if (callbackOnMain) {
                                mainThread { callbackBlock() }
                            } else {
                                callbackBlock()
                            }
                        }
                    }
            }
            if(doClean) {
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

    /**
     * 根据topic设置同步锁
     * @param topic String
     * @param block Function0<Unit>
     */
    private inline fun lock(topic: String, block: () -> Unit) {
        if(threadLock) {
            synchronized("_MQTT$$${topic}".intern(), block)
        } else {
            block()
        }
    }
}