@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket.dsl

import com.gitee.xuankaicat.kmnkt.socket.Communicate
import com.gitee.xuankaicat.kmnkt.socket.MQTT
import com.gitee.xuankaicat.kmnkt.socket.MQTTCommunicate

/**
 * 构造UDP
 * @param builder 构建器
 * @return UDP
 */
fun udp(builder: Communicate.() -> Unit)
= Communicate.UDP.apply(builder)

/**
 * 构造TCPClient
 * @param builder 构建器
 * @return TCP
 */
fun tcp(builder: Communicate.() -> Unit)
= Communicate.TCPClient.apply(builder)

/**
 * 构造MQTT
 * @param builder 构建器
 * @return MQTT
 */
fun mqtt(builder: MQTTCommunicate.() -> Unit)
= Communicate.MQTT.apply(builder)