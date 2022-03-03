package com.gitee.xuankaicat.communicate.aliyuniot

import com.gitee.xuankaicat.communicate.MQTT
import com.gitee.xuankaicat.communicate.MqttQuality
import java.util.concurrent.atomic.AtomicInteger

class AlinkMQTT(
    private val aliyunMqtt: AliyunMqtt,
): MQTT() {
    private val id: AtomicInteger = AtomicInteger(0)
    val nextId
        get() = id.incrementAndGet().toString()

    val deviceName
        get() = aliyunMqtt.deviceName

    val productKey
        get() = aliyunMqtt.productKey

    val deviceSecret
        get() = aliyunMqtt.deviceSecret

    init {
        val timestamp = CreateHelper.timestamp()
        qos = MqttQuality.AtLeastOnce // 阿里云不支持qos(2)
        port = 443
        uriType = "ssl"
        address = "${productKey}.iot-as-mqtt.${aliyunMqtt.regionId}.aliyuncs.com"
        username = "${deviceName}&${productKey}"
        password = aliyunMqtt.getSign(timestamp)
        clientId = "${productKey}.${deviceName}|" +
                "timestamp=${timestamp}" +
                ",_v=paho-java-1.0.0,securemode=2,signmethod=hmacsha256|"
    }
}