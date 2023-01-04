package com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation

/**
 * MQTT
 * 订阅指定Topic
 * @property value 订阅的Topic
 * @constructor
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Subscribe(
    /**
     * topic
     */
    val value: String
)
