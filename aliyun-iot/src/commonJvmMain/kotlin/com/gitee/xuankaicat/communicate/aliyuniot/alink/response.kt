package com.gitee.xuankaicat.communicate.aliyuniot.alink

import com.gitee.xuankaicat.communicate.MQTTCommunicate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun MQTTCommunicate.alinkResponse(
    topic: String,
    alinkBase: AlinkBase
) = send(topic, Json.encodeToString(alinkBase))