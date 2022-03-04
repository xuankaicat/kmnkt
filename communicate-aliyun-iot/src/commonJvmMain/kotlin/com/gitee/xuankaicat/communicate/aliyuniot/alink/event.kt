@file:Suppress("unused")

package com.gitee.xuankaicat.communicate.aliyuniot.alink

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.communicate.aliyuniot.utils.OnReceiveAlinkRequestFunc
import com.gitee.xuankaicat.communicate.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.communicate.aliyuniot.utils.sendAndReceiveAlink
import com.gitee.xuankaicat.communicate.aliyuniot.utils.toJsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * 设备属性上报
 * @receiver MQTTCommunicate
 * - [设备属性、事件、服务](https://help.aliyun.com/document_detail/89301.html)
 */
fun MQTTCommunicate.propertyPost(
    params: Map<String, Any>,
    onReceive: OnReceiveAlinkResultFunc = {}
) {
    this as AlinkMQTT

    val id = nextId
    val msgObj = AlinkBase(id,
        params = params.toJsonObject(),
        method = "thing.event.property.post"
    )

    sendAndReceiveAlink(id, "/sys/${productKey}/${deviceName}/thing/event/property/post", msgObj, onReceive)
}

/**
 * 设备属性设置
 * @receiver MQTTCommunicate
 * @param receiveOnce 是否只接收一次，默认为false表示一直接收
 * - [设备属性、事件、服务](https://help.aliyun.com/document_detail/89301.html)
 */
fun MQTTCommunicate.propertySet(
    receiveOnce: Boolean = false,
    onReceive: OnReceiveAlinkRequestFunc = {},
) {
    this as AlinkMQTT

    addInMessageTopic("/sys/gvjbFCd19iJ/${deviceName}/thing/service/property/set") { v, _ ->
        onReceive(Json.decodeFromString(v))
        return@addInMessageTopic !receiveOnce
    }
}