@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

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

interface MQTTCommunicate : Communicate {
    companion object {
        @JvmStatic
        val MQTT: MQTTCommunicate
            @JvmName("MQTT")
            get() = MQTT()

        /**
         * 构造MQTT
         * @param build 构造lambda
         * @return MQTT
         */
        @JvmStatic
        fun getMQTT(build: (MQTTCommunicate) -> Unit): MQTTCommunicate = MQTT.apply(build)
    }

    /**
     * 服务质量，默认为ExactlyOnce(2)
     */
    var qos: MqttQuality

    /**
     * 连接超时时间，超过这个时间未连接将进行失败回调
     * 默认为10秒
     */
    var timeOut: Int

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
     * 发送指定topic的数据
     * @param topic 主题
     * @param message 数据内容
     */
    fun send(topic: String, message: String)
}

val Communicate.Companion.MQTT
    get() = MQTTCommunicate.MQTT