@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket
import com.gitee.xuankaicat.kmnkt.aliyuniot.AlinkMQTT
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.OnReceiveAlinkResultFunc
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.sendAndReceiveAlink
import com.gitee.xuankaicat.kmnkt.aliyuniot.utils.toJsonObject

/**
 * 设备上报标签数据
 * @receiver MQTTCommunicate
 * @param expectResponse 期待服务端返回信息
 * - [设备标签](https://help.aliyun.com/document_detail/89304.html)
 */
fun IMqttSocket.deviceInfoUpdate(
    params: Map<String, Any>,
    expectResponse: Boolean = false,
    onReceive: OnReceiveAlinkResultFunc = {}
) {
    this as AlinkMQTT

    val id = nextId
    val msgObj = AlinkBase(id,
        params = params.toJsonObject(),
        sys = AlinkBase.Sys(if(expectResponse) "1" else "0"),
        method = "thing.deviceinfo.update"
    )

    sendAndReceiveAlink(id, "/sys/${productKey}/${deviceName}/thing/deviceinfo/update", msgObj, false, onReceive)
}