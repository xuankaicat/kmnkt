@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import org.eclipse.paho.client.mqttv3.MqttConnectOptions

/**
 * Mqtt发送质量(qos)枚举
 */
enum class MqttQuality {
    /**
     * Sender 发送的一条消息，Receiver 最多能收到一次
     */
    AtMostOnce,

    /**
     * Sender 发送的一条消息，Receiver 至少能收到一次
     */
    AtLeastOnce,

    /**
     * Sender 发送的一条消息，Receiver 确保能收到而且只收到一次
     * Sender 尽力向 Receiver 发送消息，如果发送失败，会继续重试，直到 Receiver 收到消息为止
     * 同时保证 Receiver 不会因为消息重传而收到重复的消息。
     */
    ExactlyOnce,
}

interface IMqttSocket : ISocket {
    companion object {
        @JvmStatic
        val MQTT: IMqttSocket
            @JvmName("MQTT")
            get() = MQTT()

        /**
         * 构造MQTT
         * @param build 构造lambda
         * @return MQTT
         */
        @JvmStatic
        fun getMQTT(build: (IMqttSocket) -> Unit): IMqttSocket = MQTT.apply(build)
    }

    /**
     * 服务质量，默认为ExactlyOnce(2)
     */
    var qos: MqttQuality

    /**
     * 通信方式
     * 默认为tcp
     */
    var uriType: String

    /**
     * 连接超时时间，超过这个时间未连接将进行失败回调
     * 默认为10秒
     */
    var timeOut: Int

    /**
     * 断开连接后是否清楚缓存，如果清除缓存则在重连后需要手动恢复订阅。默认清除缓存。
     */
    var cleanSession: Boolean

    /**
     * 检测连接是否中断的间隔。
     * 默认为20秒
     */
    var keepAliveInterval: Int

    /**
     * 用户名
     */
    var username: String

    /**
     * 密码
     */
    var password: String

    /**
     * 客户端ID，如果为空则为随机值
     */
    var clientId: String

    /**
     * 输入信息响应主题，即接收对应主题的消息。
     */
    var inMessageTopic: String

    /**
     * 输出信息响应主题，即发送对应主题的消息。
     */
    var outMessageTopic: String

    /**
     * MQTT参数设置，非定制不需要修改
     */
    var options: MqttConnectOptions?

    /**
     * 增加输入信息响应主题
     * @param topic 主题
     * @param onReceive 回调函数
     */
    fun addInMessageTopic(topic: String, onReceive: OnReceiveFunc)

    /**
     * 移除输入信息响应主题
     * @param topic 主题
     */
    fun removeInMessageTopic(topic: String)

    /**
     * 同步发送数据
     * @param message 数据内容
     */
    fun sendSync(message: String)

    /**
     * 同步发送数据
     * @param topic 主题
     * @param message 数据内容
     */
    fun sendSync(topic: String, message: String)

    /**
     * 发送指定topic的数据
     * @param topic 主题
     * @param message 数据内容
     */
    fun send(topic: String, message: String)

    /**
     * 循环发送指定topic的数据
     * @param topic 主题
     * @param message 数据内容
     * @param times 发送次数，-1表示持续发送
     * @param delay 发送间隔，单位为毫秒
     * @return 执行发送数据的线程
     * > 想要在发送期间停止发送数据可以对返回的线程对象调用`interrupt()`函数
     */
    fun send(topic: String, message: String, times: Int, delay: Long): Thread

    /**
     * 发送指定发送主题数据并根据接收主题接收消息
     * @param outTopic 发送主题
     * @param inTopic 接收主题
     * @param message 数据内容
     * @param onReceive 回调函数
     */
    fun sendAndReceive(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc)

    /**
     * 同步发送指定发送主题数据并根据接收主题接收消息
     * @param outTopic 发送主题
     * @param inTopic 接收主题
     * @param message 数据内容
     * @param onReceive 回调函数
     */
    fun sendAndReceiveSync(outTopic: String, inTopic: String, message: String, onReceive: OnReceiveFunc)
}

/**
 * 发送指定发送主题数据并根据接收主题接收消息
 * @param outTopic 发送主题
 * @param inTopic 接收主题
 * @param message 数据内容
 * @param onReceive 回调函数
 */
inline fun IMqttSocket.sendAndReceive(outTopic: String, inTopic: String, message: String, crossinline onReceive: OnReceiveSimpleFunc) =
    this.sendAndReceive(outTopic, inTopic, message) { v, _ -> onReceive(v) }

/**
 * 同步发送指定发送主题数据并根据接收主题接收消息
 * @param outTopic 发送主题
 * @param inTopic 接收主题
 * @param message 数据内容
 * @param onReceive 回调函数
 */
inline fun IMqttSocket.sendAndReceiveSync(outTopic: String, inTopic: String, message: String, crossinline onReceive: OnReceiveSimpleFunc) =
    this.sendAndReceiveSync(outTopic, inTopic, message) { v, _ -> onReceive(v) }

val ISocket.Companion.MQTT
    get() = IMqttSocket.MQTT