package com.example.springbootdemo.schedule

import com.example.springbootdemo.service.IMqttService
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import javax.annotation.Resource

/**
 * 在定时任务中使用示例
 */
@Configuration
@EnableScheduling
class MQTTScheduleTask {
    @Resource
    private lateinit var mqttService: IMqttService

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 去掉注释后每五分钟执行一次
     */
    //@org.springframework.scheduling.annotation.Scheduled(initialDelay = 5000, fixedRate = 300000)
    @Suppress("unused")
    fun switchTask() {
        val result = mqttService.getInfo(
            "DeviceTest/111111",
            "DeviceTest/123456",
            "{cmd:\"info\"}"
        ).get()
        logger.info(result)
    }

}