package com.gitee.xuankaicat.communicate.aliyuniot.utils

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun Map<String, Any>.toJsonObject(): JsonElement =
    JsonObject(this.mapValues {
        when (it.value) {
            is Map<*, *> ->
                @Suppress("UNCHECKED_CAST") (it.value as Map<String, Any>).toJsonObject()
            is String ->
                JsonPrimitive(it.value as String)
            is Number ->
                JsonPrimitive(it.value as Number)
            is Boolean ->
                JsonPrimitive(it.value as Boolean)
            else -> JsonNull
        }
    })