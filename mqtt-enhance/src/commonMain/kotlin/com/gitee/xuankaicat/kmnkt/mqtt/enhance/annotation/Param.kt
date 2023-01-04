package com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation

/**
 * MQTT
 * 指定函数订阅注解中的参数对象,形成通配符+
 * @property value 参数名
 * @constructor
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Param (
    /**
     * 参数名
     */
    val value: String = ""
)

/**
 * MQTT
 * 指定函数订阅注解中的参数对象,形成通配符#
 * @property value 参数名
 * @constructor
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class AnyParam (
    /**
     * 参数名
     */
    val value: String = ""
)