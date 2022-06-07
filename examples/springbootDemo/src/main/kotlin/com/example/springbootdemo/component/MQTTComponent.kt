package com.example.springbootdemo.component

import com.gitee.xuankaicat.kmnkt.socket.MqttQuality
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class MQTTComponent {
    final val instance = mqtt {
        address = "127.0.0.1"//设置ip地址
        port = 1883
        qos = MqttQuality.AtMostOnce
        callbackOnMain = false
        inCharset = Charset.forName("utf-8")
        outCharset = Charset.forName("utf-8")
        username = "xuankai"
        password = "xuankai"
    }

    init {
        instance.open()
    }
}