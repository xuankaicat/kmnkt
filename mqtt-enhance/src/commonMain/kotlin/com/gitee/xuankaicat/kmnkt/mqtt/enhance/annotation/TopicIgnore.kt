@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation

/**
 * 需要无视的Topic的匹配方式枚举
 */
enum class TopicIgnoreType {
    /**
     * 默认方式，匹配接收到的Topic的尾部
     */
    DEFAULT,

    /**
     * 全字匹配
     */
    FULL,

    /**
     * 正则表达式匹配
     */
    REGEX,
}

/**
 * 指定需要无视的Topic
 * - 需要在已经使用了Subscribe注解的函数上使用
 * - Topic仍然会被接收到但是不会做处理
 * @property value 匹配参数
 * @property type 匹配模式，默认匹配接收到的Topic的尾部
 * @see Subscribe
 * @constructor
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TopicIgnore(
    val value: String,
    val type: TopicIgnoreType = TopicIgnoreType.DEFAULT
)