package com.gitee.xuankaicat.communicate.aliyuniot

import com.gitee.xuankaicat.communicate.Communicate
import com.gitee.xuankaicat.communicate.MQTT
import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.utils.Log
import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal object CreateHelper {
     private fun hmac(plainText: String, key: String, algorithm: String, format: String): String {
        val mac = Mac.getInstance(algorithm)

        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        mac.init(secretKeySpec)

        val result = mac.doFinal(plainText.toByteArray())

        return String.format(format, BigInteger(1, result))
    }

    fun hmacSha256(plainText: String, key: String): String =
        hmac(plainText, key, "HmacSHA256", "%064x")
}

/**
 * 构造阿里云MQTT。
 * 可以根据AliyunMqtt生成port uriType address username password clientId。
 * 对于新版公共实例和企业版实例需设置address为企业版实例下MQTT接入域名。
 * @param builder 构建器
 * @return MQTT
 */
fun mqtt(aliyunMqtt: AliyunMqtt, builder: MQTTCommunicate.() -> Unit): MQTTCommunicate {
    val timestamp = System.currentTimeMillis().toString()
    return Communicate.MQTT.apply {
        port = 443
        uriType = "ssl"
        address = "${aliyunMqtt.productKey}.iot-as-mqtt.${aliyunMqtt.regionId}.aliyuncs.com"
        username = "${aliyunMqtt.deviceName}&${aliyunMqtt.productKey}"
        password = CreateHelper.hmacSha256(
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

data class AliyunMqtt(
    val productKey: String,
    val deviceName: String,
    val deviceSecret: String,
    val regionId: String = "cn-shanghai",
)