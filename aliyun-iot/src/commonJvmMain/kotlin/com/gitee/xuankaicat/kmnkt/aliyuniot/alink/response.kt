package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import com.gitee.xuankaicat.kmnkt.socket.MQTTCommunicate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun MQTTCommunicate.alinkResponse(
    topic: String,
    alinkBase: AlinkBase
) = send(topic, Json.encodeToString(alinkBase))