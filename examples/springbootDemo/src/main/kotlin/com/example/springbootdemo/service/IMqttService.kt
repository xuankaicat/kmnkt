package com.example.springbootdemo.service

import java.util.concurrent.Future

interface IMqttService {
    fun getInfo(outTopic: String, inTopic: String, data: String): Future<String>

}