@file:Suppress("unused")

package com.gitee.xuankaicat.communicate.aliyuniot.alink

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.communicate.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.communicate.aliyuniot.utils.sendAndReceiveAlink
import com.gitee.xuankaicat.communicate.aliyuniot.utils.toJsonObject

/**
 * 设备上报属性
 * @receiver MQTTCommunicate
 * - [设备属性、事件、服务](https://help.aliyun.com/document_detail/89301.html)
 */
fun MQTTCommunicate.eventPost(
    params: Map<String, Any>,
    onReceive: OnReceiveAlinkResultFunc = {}
) {
    this as AlinkMQTT

    val msgObj = AlinkBase(nextId,
        params = params.toJsonObject(),
        method = "thing.event.property.post"
    )

    sendAndReceiveAlink("/sys/${productKey}/${deviceName}/thing/event/property/post", msgObj, onReceive)
}