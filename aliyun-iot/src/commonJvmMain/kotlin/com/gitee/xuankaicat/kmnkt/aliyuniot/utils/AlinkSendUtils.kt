package com.gitee.xuankaicat.kmnkt.aliyuniot.utils

import com.gitee.xuankaicat.kmnkt.socket.MQTTCommunicate
import com.gitee.xuankaicat.kmnkt.aliyuniot.alink.AlinkBase
import com.gitee.xuankaicat.kmnkt.aliyuniot.alink.AlinkResult
import com.gitee.xuankaicat.kmnkt.socket.sendAndReceive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias OnReceiveAlinkResultFunc = (AlinkResult) -> Unit
typealias OnReceiveAlinkRequestFunc = (AlinkBase) -> Unit

/**
 * 发送并接收alink消息
 * @see AlinkResult
 */
inline fun MQTTCommunicate.sendAndReceiveAlink(
    messageId: String,
    topic: String,
    sendObj: AlinkBase,
    repeat: Boolean = false,
    crossinline onReceive: OnReceiveAlinkResultFunc,
) = sendAndReceive(topic, topic + "_reply", Json.encodeToString(sendObj)) { v ->
    val result: AlinkResult = Json.decodeFromString(v)
    if(result.id != messageId) return@sendAndReceive true
    onReceive.invoke(result)
    return@sendAndReceive repeat
}