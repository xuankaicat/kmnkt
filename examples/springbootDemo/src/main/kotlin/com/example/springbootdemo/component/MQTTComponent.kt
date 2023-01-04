package com.example.springbootdemo.component

import com.gitee.xuankaicat.kmnkt.mqtt.enhance.MQTTManager
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.moshi.MoshiConvertFactory
import com.gitee.xuankaicat.kmnkt.socket.MqttQuality
import com.gitee.xuankaicat.kmnkt.socket.dsl.mqtt
import com.gitee.xuankaicat.kmnkt.socket.open
import org.springframework.stereotype.Component
import java.nio.charset.Charset

@Component
class MQTTComponent {
    final val instance = mqtt {
        address = "127.0.0.1"//设置ip地址
        port = 1883
        qos = MqttQuality.AtMostOnce
        callbackOnMain = false
        cleanSession = false
        inCharset = Charset.forName("utf-8")
        outCharset = Charset.forName("utf-8")
        username = "xuankai"
        password = "xuankai"
    }

    final val mqttManager = MQTTManager(instance, MoshiConvertFactory.create())

    init {
        var success = false
        instance.open {
            success {
                success = true
            }
        }
        while (!success) {
            Thread.sleep(50L)
        }
    }
}