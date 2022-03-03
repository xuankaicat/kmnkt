package com.gitee.xuankaicat.communicate.aliyuniot.utils

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import com.gitee.xuankaicat.communicate.aliyuniot.alink.AlinkBase
import com.gitee.xuankaicat.communicate.aliyuniot.alink.AlinkResult
import com.gitee.xuankaicat.communicate.sendAndReceive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

typealias OnReceiveAlinkResultFunc = (AlinkResult) -> Unit

/**
 * 发送并接收alink消息
 * @see AlinkResult
 */
internal inline fun MQTTCommunicate.sendAndReceiveAlink(
    topic: String,
    sendObj: AlinkBase,
    crossinline onReceive: OnReceiveAlinkResultFunc
) = sendAndReceive(topic, topic + "_reply", Json.encodeToString(sendObj)) { v ->
    onReceive.invoke(Json.decodeFromString(v))
    return@sendAndReceive false
}