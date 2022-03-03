package com.gitee.xuankaicat.communicate.aliyuniot.alink

import kotlinx.serialization.*

/**
 * Alink协议基本格式
 * @property id 消息ID号。需定义为String类型的数字，取值范围0~4294967295，且每个消息ID在当前设备中具有唯一性。
 * @property sys 扩展功能的参数，其下包含各功能字段。
 * @property version 协议版本号，目前协议版本号唯一取值为1.0
 * @property params 请求入参
 * @property method 请求方法
 */
@Serializable
data class AlinkBase(
    val id: String,
    @Required
    val sys: Sys = Sys(),
    @Required
    val version: String = "1.0",
    @Required
    val params: List<Map<String, String>> = listOf(),
    val method: String,
) {
    /**
     * @property ack sys下的扩展功能字段，表示是否返回响应数据。
     * @constructor
     */
    @Serializable
    data class Sys(@Required val ack: String = "0")
}
