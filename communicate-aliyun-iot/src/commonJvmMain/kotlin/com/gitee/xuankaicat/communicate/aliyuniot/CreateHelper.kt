package com.gitee.xuankaicat.communicate.aliyuniot

import com.gitee.xuankaicat.communicate.Communicate
import com.gitee.xuankaicat.communicate.MQTT
import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.utils.Log
import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object CreateHelper {
    private fun hmac(plainText: String, key: String, algorithm: String, format: String): String {
        val mac = Mac.getInstance(algorithm)

        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        mac.init(secretKeySpec)

        val result = mac.doFinal(plainText.toByteArray())

        return String.format(format, BigInteger(1, result))
    }

    private fun hmacSha256(plainText: String, key: String): String =
        hmac(plainText, key, "HmacSHA256", "%064x")

    fun mqtt(aliyunMqtt: AliyunMqtt, builder: MQTTCommunicate.() -> Unit): MQTTCommunicate {
        val timestamp = System.currentTimeMillis().toString()
        return Communicate.MQTT.apply {
            port = 443
            uriType = "ssl"
            //TODO: 支持不同地区的address
            address = "${aliyunMqtt.productKey}.iot-as-mqtt.cn-shanghai.aliyuncs.com"
            username = "${aliyunMqtt.deviceName}&${aliyunMqtt.productKey}"
            password = hmacSha256(
                "clientId${aliyunMqtt.productKey}.${aliyunMqtt.deviceName}" +
                        "deviceName${aliyunMqtt.deviceName}" +
                        "productKey${aliyunMqtt.productKey}" +
                        "timestamp${timestamp}",
                aliyunMqtt.deviceSecret
            )
            clientId = "${aliyunMqtt.productKey}.${aliyunMqtt.deviceName}|" +
                    "timestamp=${timestamp}" +
                    ",_v=paho-java-1.0.0,securemode=2,signmethod=hmacsha256|"
            Log.v("AliyunMQTT", "自动创建mqtt连接对象 " +
                    "{port: $port, uriType: '$uriType', address: '$address'" +
                    "username: '${username}', password: ${password}, clientId: ${clientId}}")
        }.apply(builder)
    }
}

data class AliyunMqtt(
    val productKey: String,
    val deviceName: String,
    val deviceSecret: String,
)