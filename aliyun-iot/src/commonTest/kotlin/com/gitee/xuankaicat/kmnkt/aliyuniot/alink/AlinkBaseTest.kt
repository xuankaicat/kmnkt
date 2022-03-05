package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class AlinkBaseTest {
    @Test
    fun serializableTest() {
        assertEquals("""
            {"id":"0","sys":{"ack":"0"},"version":"1.0","params":{"1":"a","2":"b"},"method":"test"}
        """.trimIndent(),
            Json.encodeToString(
                AlinkBase("0",
                    params = jsonObject(mapOf("1" to "a", "2" to "b")),
                    method = "test"))
        )

        assertEquals("""
            {"id":"0","sys":{"ack":"0"},"version":"1.0","params":{"1":"a","2":{"2.1":"b"}},"method":"test"}
        """.trimIndent(),
            Json.encodeToString(
                AlinkBase("0",
                    params = jsonObject(mapOf("1" to "a", "2" to mapOf("2.1" to "b"))),
                    method = "test"))
        )
    }

    fun jsonObject(content: Map<String, Any>): JsonElement =
        JsonObject(content.mapValues {
            when (it.value) {
                is Map<*, *> ->
                    @Suppress("UNCHECKED_CAST") jsonObject(it.value as Map<String, Any>)
                is String ->
                    JsonPrimitive(it.value as String)
                else -> throw Exception()
            }
        })
}