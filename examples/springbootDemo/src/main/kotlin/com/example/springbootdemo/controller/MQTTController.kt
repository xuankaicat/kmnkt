package com.example.springbootdemo.controller

import com.example.springbootdemo.component.MQTTComponent
import com.example.springbootdemo.model.MyUser
import com.example.springbootdemo.service.IMqttService
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation.AnyParam
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation.Param
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation.Payload
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.annotation.Subscribe
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

/**
 * 在控制器中使用示例
 */
@RestController
@RequestMapping("/mqtt")
class MQTTController(
    mqttComponent: MQTTComponent,
    val mqttService: IMqttService
) {

    init {
        mqttComponent.mqttManager.enable(MQTTController::class, this)
    }

    @GetMapping("/info")
    fun getInfo(): String = mqttService.getInfo(
        "DeviceTest/111111",
        "DeviceTest/123456",
        "{cmd:\"info\"}"
    ).get()

    @Subscribe("service/time")
    fun mqttGetTime(topic: String, @Payload payload: String) {
        mqttService.sendData("${topic}/reply", """{
            "time": "${LocalTime.now()}"
        }""".trimIndent())
    }

    @Subscribe("service/user/{id}")
    fun mqttGetUsername(topic: String, @Payload payload: String, @Param id: Int) {
        val name = when(id) {
            1 -> "first"
            2 -> "second"
            else -> "else"
        }
        mqttService.sendData("${topic}/reply", """{
            "id": $id,
            "name": "$name",
            "time": "${LocalTime.now()}"
        }""".trimIndent())
    }

    @Subscribe("service/extra/{extra}")
    fun mqttPhaseExtraPath(topic: String, @Payload myUser: MyUser, @AnyParam extra: List<String>) {
        if(topic.endsWith("reply")) return
        mqttService.sendData("${topic}/reply", """{
            "id": ${myUser.id},
            "name": "${myUser.name}",
            "extra": "$extra",
            "fullTopic": "$topic",
            "time": "${LocalTime.now()}"
        }""".trimIndent())
    }
}