package com.gitee.xuankaicat.communicate.aliyuniot.alink

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AlinkBaseTest {
    @Test
    fun serializableTest() {
        assertEquals("""
            {"id":"0","sys":{"ack":"0"},"version":"1.0","params":[],"method":"test"}
        """.trimIndent(),
            Json.encodeToString(
                AlinkBase("0",
                    params = listOf(),
                    method = "test"))
        )
    }
}