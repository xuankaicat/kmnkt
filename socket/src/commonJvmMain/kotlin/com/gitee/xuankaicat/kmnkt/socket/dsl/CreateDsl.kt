@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket.dsl

import com.gitee.xuankaicat.kmnkt.socket.ISocket
import com.gitee.xuankaicat.kmnkt.socket.MQTT
import com.gitee.xuankaicat.kmnkt.socket.IMqttSocket

/**
 * 构造UDP
 * @param builder 构建器
 * @return UDP
 */
fun udp(builder: ISocket.() -> Unit)
= ISocket.UDP.apply(builder)

/**
 * 构造TCPClient
 * @param builder 构建器
 * @return TCP
 */
fun tcp(builder: ISocket.() -> Unit)
= ISocket.TCPClient.apply(builder)

/**
 * 构造MQTT
 * @param builder 构建器
 * @return MQTT
 */
fun mqtt(builder: IMqttSocket.() -> Unit)
= ISocket.MQTT.apply(builder)