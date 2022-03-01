package com.gitee.xuankaicat.communicate.aliyuniot

import com.gitee.xuankaicat.communicate.MQTT
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

    fun getSign(timestamp: String = CreateHelper.timestamp()) = CreateHelper.hmacSha256(
        "clientId${productKey}.${deviceName}" +
                "deviceName${deviceName}" +
                "productKey${productKey}" +
                "timestamp${timestamp}",
        deviceSecret
    )

    init {
        val timestamp = CreateHelper.timestamp()
        port = 443
        uriType = "ssl"
        address = "${productKey}.iot-as-mqtt.${aliyunMqtt.regionId}.aliyuncs.com"
        username = "${deviceName}&${productKey}"
        password = getSign(timestamp)
        clientId = "${productKey}.${deviceName}|" +
                "timestamp=${timestamp}" +
                ",_v=paho-java-1.0.0,securemode=2,signmethod=hmacsha256|"
    }
}