package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun IMqttSocket.alinkResponse(
    topic: String,
    alinkBase: AlinkBase
) = send(topic, Json.encodeToString(alinkBase))