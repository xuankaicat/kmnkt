@file:Suppress("unused")

package com.gitee.xuankaicat.communicate.aliyuniot.alink

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.communicate.aliyuniot.AliyunMqtt
import com.gitee.xuankaicat.communicate.aliyuniot.CreateHelper
import com.gitee.xuankaicat.communicate.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.communicate.aliyuniot.utils.sendAndReceiveAlink

/**
 * 添加设备拓扑关系
 * @receiver MQTTCommunicate
 * - [管理拓扑关系](https://help.aliyun.com/document_detail/89299.htm)
 */
fun MQTTCommunicate.topoAdd(aliyunMqtt: AliyunMqtt, clientId: String? = null, onReceive: OnReceiveAlinkResultFunc) {
    this as AlinkMQTT

    val timestamp = CreateHelper.timestamp()
    val param = mapOf(
        "deviceName" to aliyunMqtt.deviceName,
        "productKey" to aliyunMqtt.productKey,
        "sign" to aliyunMqtt.getSign(timestamp),
        "signmethod" to "hmacSha256",
        "timestamp" to timestamp,
        "clientId" to (clientId ?: "${aliyunMqtt.productKey}&${aliyunMqtt.deviceName}"),
    )

    val msgObj = AlinkBase(nextId,
        params = listOf(param),
        method = "thing.topo.add"
    )

    sendAndReceiveAlink("/sys/$productKey/$deviceName/thing/topo/add", msgObj, onReceive)
}