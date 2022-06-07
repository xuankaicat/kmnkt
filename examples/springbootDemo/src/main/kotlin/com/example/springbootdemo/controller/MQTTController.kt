package com.example.springbootdemo.controller

import com.example.springbootdemo.service.IMqttService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.Resource

/**
 * 在控制器中使用示例
 */
@RestController
@RequestMapping("/mqtt")
class MQTTController {
    @Resource
    private lateinit var mqttService: IMqttService

    @GetMapping("/info")
    fun getInfo(): String = mqttService.getInfo(
        "DeviceTest/111111",
        "DeviceTest/123456",
        "{cmd:\"info\"}"
    ).get()

}