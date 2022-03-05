@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket
import com.gitee.xuankaicat.kmnkt.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.kmnkt.aliyuniot.AliyunMqtt
import com.gitee.xuankaicat.kmnkt.aliyuniot.CreateHelper
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.sendAndReceiveAlink
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.toJsonObject

/**
 * 添加设备拓扑关系
 * @receiver MQTTCommunicate
 * @param expectResponse 期待服务端返回信息
 * - [管理拓扑关系](https://help.aliyun.com/document_detail/89299.htm)
 */
fun IMqttSocket.topoAdd(
    aliyunMqtt: AliyunMqtt,
    clientId: String? = null,
    expectResponse: Boolean = false,
    onReceive: OnReceiveAlinkResultFunc = {}
) {
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

    val id = nextId
    val msgObj = AlinkBase(id,
        params = param.toJsonObject(),
        sys = AlinkBase.Sys(if(expectResponse) "1" else "0"),
        method = "thing.topo.add"
    )

    sendAndReceiveAlink(id, "/sys/$productKey/$deviceName/thing/topo/add", msgObj, false, onReceive)
}