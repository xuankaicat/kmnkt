package com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.moshi

import com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.Converter
import com.squareup.moshi.JsonAdapter

class MoshiPayloadConverter<T>(
    private val adapter: JsonAdapter<T>
) : Converter<String, T> {
    override fun convert(from: String): T? = adapter.fromJson(from)
}