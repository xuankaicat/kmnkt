package com.gitee.xuankaicat.kmnkt.socket

expect class TCP() : ISocket
expect class UDP() : ISocket, ISendWithPort
expect class MQTT() : IMqttSocket