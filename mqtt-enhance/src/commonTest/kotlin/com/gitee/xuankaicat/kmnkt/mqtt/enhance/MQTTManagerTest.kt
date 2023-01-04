package com.gitee.xuankaicat.kmnkt.mqtt.enhance

import org.junit.Test
import kotlin.test.assertEquals

class MQTTManagerTest {
    @Test
    fun replaceTopicTest() {
        val topic = "test/{x}/{device}/hex/233/{final}"

        val paramIndexs = hashMapOf(
            "x" to Pair(1, Int::class.java as Class<*>),
            "device" to Pair(2, Int::class.java)
        )

        val anyParam = Pair("final", 3)

        val replacedTopic = MQTTManager.Subscriber.replaceTopic(topic, paramIndexs, anyParam)

        assertEquals("test/+/+/hex/233/#", replacedTopic)
    }
}