package com.gitee.xuankaicat.kmnkt.aliyuniot.alink

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

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
    val sys: Sys = Sys(),
    @Required
    val version: String = "1.0",
    @Required
    val params: JsonElement = JsonObject(mapOf()),
    val method: String,
) {
    /**
     * @property ack sys下的扩展功能字段，表示是否返回响应数据。
     * @constructor
     */
    @Serializable
    data class Sys(@Required val ack: String = "0")
}

/**
 * Alink协议通用返回结果
 * @property id 消息ID
 * @property code 返回结果，200代表成功。
 * @property data 请求成功时的返回结果。
 * @constructor
 */
@Serializable
data class AlinkResult(
    val id: String,
    val code: Int,
    val data: JsonObject,
) {
    var message: String? = null
    var method: String? = null
    var version: String? = null
}