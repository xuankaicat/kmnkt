@file:Suppress("unused", "SameParameterValue")

package com.gitee.xuankaicat.kmnkt.aliyuniot

import com.gitee.xuankaicat.kmnkt.socket.MQTTCommunicate
import com.gitee.xuankaicat.kmnkt.socket.utils.Log
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

    fun timestamp() = System.currentTimeMillis().toString()
}

/**
 * 构造阿里云MQTT。
 * 可以根据AliyunMqtt生成port uriType address username password clientId。
 * 对于新版公共实例和企业版实例需设置address为企业版实例下MQTT接入域名。
 * @param builder 构建器
 * @return MQTT
 */
fun mqtt(aliyunMqtt: AliyunMqtt, builder: MQTTCommunicate.() -> Unit = {}): MQTTCommunicate {
    return AlinkMQTT(aliyunMqtt).apply {
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
) {
    fun getSign(timestamp: String = CreateHelper.timestamp()) = CreateHelper.hmacSha256(
        "clientId${productKey}.${deviceName}" +
                "deviceName${deviceName}" +
                "productKey${productKey}" +
                "timestamp${timestamp}",
        deviceSecret
    )
}