package com.gitee.xuankaicat.communicate.aliyuniot.alink

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.communicate.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.communicate.aliyuniot.utils.sendAndReceiveAlink
import com.gitee.xuankaicat.communicate.aliyuniot.utils.toJsonObject

/**
 * 设备上报标签数据
 * @receiver MQTTCommunicate
 * - [设备标签](https://help.aliyun.com/document_detail/89304.html)
 */
fun MQTTCommunicate.deviceInfoUpdate(
    params: Map<String, Any>,
    onReceive: OnReceiveAlinkResultFunc = {}
) {
    this as AlinkMQTT

    val id = nextId
    val msgObj = AlinkBase(id,
        params = params.toJsonObject(),
        method = "thing.deviceinfo.update"
    )

    sendAndReceiveAlink(id, "/sys/${productKey}/${deviceName}/thing/deviceinfo/update", msgObj, onReceive)
}