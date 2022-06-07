package com.example.springbootdemo.service.impl

import com.example.springbootdemo.component.MQTTComponent
import com.example.springbootdemo.service.IMqttService
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.AsyncResult
import org.springframework.stereotype.Service
import java.util.concurrent.Future
import javax.annotation.Resource

@Service
class MqttServiceImpl : IMqttService {
    @Resource
    private lateinit var mqtt: MQTTComponent

    @Async
    override fun getInfo(outTopic: String, inTopic: String, data: String): Future<String> = AsyncResult(
        mqtt.instance.sendAndReceiveSync(
            outTopic,
            inTopic,
            data,
            10000L
        ) ?: "操作超时"
    )

}