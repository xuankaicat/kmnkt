@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.mqtt.enhance

import com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation.*
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.PayloadConvertFactory
import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.reflect.KClass

class MQTTManager(
    val mqtt: IMqttSocket,
    var convertFactory: PayloadConvertFactory? = null
) {
    val subscribers = LinkedList<Subscriber>()

    inline fun <reified T : Any> enable(service: KClass<T>) = enable(service.java)
    inline fun <reified T : Any> enable(service: KClass<T>, instance: T) = enable(service.java, instance)

    inline fun <reified T : Any> enable(service: Class<T>) =
        enable(service, T::class.java.getDeclaredConstructor().newInstance())

    inline fun <reified T : Any> enable(service: Class<T>, instance: T) {
        val serviceName = service.name
        service.methods.forEach { method ->
            if (method.isAnnotationPresent(Subscribe::class.java)) {
                val subscriber = Subscriber(mqtt, serviceName, method, instance, convertFactory)
                subscribers.add(subscriber)
            }
        }
    }

    fun <T : Any> disable(service: KClass<T>) = disable(service.java)

    fun <T> disable(service: Class<T>) {
        val serviceName = service.name
        subscribers.removeIf { it.serviceName == serviceName }
    }

    class Subscriber(
        mqtt: IMqttSocket,
        val serviceName: String,
        val method: Method,
        val instance: Any,
        val convertFactory: PayloadConvertFactory?
    ) {
        companion object {
            private val PARAM_PATTERN = "\\{(\\w+)}".toRegex()
            private const val EXPECT_PATTERN_STRING = "([^/]+)"
            private const val ANY_PARAM_PATTERN_STRING = "(\\S*)"

            fun replaceTopic(
                topic: String,
                paramIndexs: HashMap<String, Pair<Int, Class<*>>>,
                anyParam: Pair<String, Int>,
                onParamReceived: (Pair<Int, Class<*>>) -> Unit = {},
            ) = PARAM_PATTERN.replace(topic) { matchResult ->
                val name = matchResult.groupValues.last()
                if (paramIndexs.containsKey(name)) {
                    onParamReceived(paramIndexs[name]!!)
                    // 替换通配符+
                    "+"
                } else if (anyParam.first == name) {
                    onParamReceived(Pair(anyParam.second, String::class.java))
                    // 替换通配符#
                    "#"
                } else {
                    throw Exception("无法被替换的参数, caused by $name")
                }
            }

            fun generateTopicPhasePattern(
                topic: String,
                anyParam: Pair<String, Int>,
            ) = PARAM_PATTERN.replace(topic) { matchResult ->
                if (matchResult.groupValues.last() == anyParam.first) {
                    ANY_PARAM_PATTERN_STRING
                } else {
                    EXPECT_PATTERN_STRING
                }
            }.toRegex()

        }

        private var payloadIndex = -1
        private var topicIndex = -1
        private val paramList = mutableListOf<Pair<Int, Class<*>>>()
        private val maxIndex: Int

        private var anyParamIsList = false
        var anyParam: Pair<String, Int> = Pair("", -1)

        private var needConverter = false
        var payloadType: Class<*>? = null

        val topicPhasePattern: Regex

        val topicIgnoreFunc: (String) -> Boolean

        init {
            val subscribe = method.getAnnotation(Subscribe::class.java)

            val parameterTypes = method.parameterTypes
            val paramIndexs = hashMapOf<String, Pair<Int, Class<*>>>()

            val parameterAnnotations = method.parameterAnnotations

            maxIndex = parameterTypes.size - 1

            for (i in parameterTypes.indices) {
                val type = parameterTypes[i]
                parameterAnnotations[i]?.let { annotations ->
                    if (annotations.isEmpty()) {
                        if (type == String::class.java) {
                            if (topicIndex == -1) {
                                // 第一个遇到的无注解字符串默认为Topic
                                topicIndex = i
                            } else {
                                throw Exception("过多参数, caused by ${method.parameters[i].name}")
                            }
                        }
                        return@let
                    }
                    for (annotation in annotations) {
                        when (annotation) {
                            is Payload -> {
                                if (payloadIndex != -1) {
                                    throw Exception("单个函数只允许出现一次Payload注解, caused by ${method.parameters[i].name}")
                                }
                                if (type != String::class.java) {
                                    needConverter = true
                                    payloadType = type
                                    if (convertFactory == null)
                                        throw Exception("需要将类型转换为$type,但没有在MQTTManager中指定convertFactory")
                                }
                                payloadIndex = i
                            }

                            is Param -> {
                                if (annotation.value == "") paramIndexs[method.parameters[i].name] = Pair(i, type)
                                else paramIndexs[annotation.value] = Pair(i, type)
                            }

                            is AnyParam -> {
                                if (i != parameterTypes.size - 1) {
                                    throw Exception("AnyParam注解必须是最后一个参数的注解, caused by ${method.parameters[i].name}")
                                }
                                if (anyParam.second != -1) {
                                    throw Exception("单个函数只允许出现一次AnyParam注解, caused by ${method.parameters[i].name}")
                                }
                                if (annotation.value == "") {
                                    anyParam = Pair(method.parameters[i].name, i)
                                } else {
                                    anyParam = Pair(annotation.value, i)
                                }

                                if (type == List::class.java) {
                                    anyParamIsList = true
                                }
                            }
                        }
                    }
                }
            }

            if (payloadIndex == -1) {
                payloadIndex = 0
                if (topicIndex == 0) topicIndex = -1
            }

            val subscribeTopic = subscribe!!.value

            val replacedTopic = replaceTopic(subscribeTopic, paramIndexs, anyParam) {
                paramList.add(it)
            }

            topicPhasePattern = generateTopicPhasePattern(subscribeTopic, anyParam)

            topicIgnoreFunc = if(method.isAnnotationPresent(TopicIgnore::class.java)) {
                val topicIgnore = method.getAnnotation(TopicIgnore::class.java)!!
                val value = topicIgnore.value
                when(topicIgnore.type) {
                    TopicIgnoreType.DEFAULT -> {{ topic: String ->
                        topic.endsWith(value)
                    }}
                    TopicIgnoreType.FULL -> {{ topic: String ->
                        topic == value
                    }}
                    TopicIgnoreType.REGEX -> {{ topic: String ->
                        value.toRegex().containsMatchIn(topic)
                    }}
                }
            } else {
                {false}
            }

            mqtt.addInMessageTopic(replacedTopic) callback@{ message, data ->
                // 判断是否需要处理
                val topic = data as String
                if(topicIgnoreFunc(data)) {
                    mqtt.Log.v("MQTT", "由TopicIgnore规则无视来自[$data]的消息")
                    return@callback true
                }

                val params = Array<Any?>(maxIndex + 1) { null }
                try {
                    params[payloadIndex] = if (needConverter) {
                        convertFactory!!.getConverter(payloadType!!).convert(message)
                    } else {
                        message
                    }
                    params[topicIndex] = topic

                    // 分析topic
                    if (paramList.size > 0) {
                        val matchResults = topicPhasePattern.findAll(topic)
                        var i = 0

                        matchResults.iterator().forEach { matchResult ->
                            val paramIndex = paramList[i++]
                            if (paramIndex.first == anyParam.second && anyParamIsList) {
                                params[paramIndex.first] = matchResult.groupValues.last().split("/")
                            } else {
                                val value = matchResult.groupValues.last()
                                params[paramIndex.first] = when (paramIndex.second) {
                                    String::class.java -> value
                                    Byte::class.java -> value.toByte()
                                    Short::class.java -> value.toShort()
                                    Int::class.java -> value.toInt()
                                    Long::class.java -> value.toLong()
                                    Float::class.java -> value.toFloat()
                                    Double::class.java -> value.toDouble()
                                    Boolean::class.java -> value.toDouble()
                                    UByte::class.java -> value.toUByte()
                                    UShort::class.java -> value.toUShort()
                                    UInt::class.java -> value.toUInt()
                                    ULong::class.java -> value.toULong()
                                    BigInteger::class.java -> value.toBigInteger()
                                    BigDecimal::class.java -> value.toBigDecimal()
                                    Char::class.java -> value[0]
                                    else -> convertFactory!!.getConverter(paramIndex.second).convert(value)
                                }
                            }
                        }
                    }
                } catch (e: InvocationTargetException) {
                    e.targetException.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                method.invoke(instance, *params)

                // 继续接收消息
                true
            }
        }
    }
}