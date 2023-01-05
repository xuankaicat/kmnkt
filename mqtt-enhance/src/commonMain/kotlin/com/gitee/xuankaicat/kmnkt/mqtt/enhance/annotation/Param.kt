package com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation

/**
 * MQTT
 * 指定函数订阅注解中的参数对象,形成通配符+
 * @property value 参数名，则默认取与函数形参同名的参数
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
 * > 只能在函数的最后一个参数上使用此注解。
 * - 可以将参数类型设置为String作为字符串接收额外参数
 * - 可以将参数类型设置为String类型的List以将额外参数分割接收
 * @property value 参数名，则默认取与函数形参同名的参数
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