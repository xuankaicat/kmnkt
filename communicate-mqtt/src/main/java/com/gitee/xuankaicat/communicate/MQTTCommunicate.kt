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
            get() = MQTT()
    }

    /**
     * 服务质量，默认为ExactlyOnce(2)
     */
    var qos: MqttQuality

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
     * 发布主题
     */
    var publishTopic: String

    /**
     * 响应主题
     */
    var responseTopic: String

    /**
     * MQTT参数设置，非定制不需要修改
     */
    var options: MqttConnectOptions?
}