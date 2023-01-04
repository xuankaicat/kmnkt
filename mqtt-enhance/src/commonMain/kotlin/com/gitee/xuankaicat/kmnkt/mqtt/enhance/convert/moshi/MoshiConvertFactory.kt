@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.moshi

import com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.Converter
import com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert.PayloadConvertFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MoshiConvertFactory private constructor(
    private val moshi: Moshi,
    private val lenient: Boolean,
    private val failOnUnknown: Boolean,
    private val serializeNulls: Boolean,
) : PayloadConvertFactory {
    companion object {
        /**
         * 创建一个使用默认*Moshi* 实例的转换器
         * @return MoshiPayloadConvertFactory
         */
        fun create() = create(Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build())

        /**
         * 创建一个使用自定义*Moshi* 实例的转换器
         * @return MoshiPayloadConvertFactory
         */
        fun create(moshi: Moshi) = MoshiConvertFactory(moshi,
            lenient = false,
            failOnUnknown = false,
            serializeNulls = false
        )
    }

    /**
     * 创建一个对JSON序列化比较宽松的转换器
     * @see com.squareup.moshi.JsonAdapter.lenient
     * @return MoshiPayloadConvertFactory
     */
    fun asLenient() = MoshiConvertFactory(moshi, true, failOnUnknown, serializeNulls)
    /**
     * 创建接收到未知值会发生异常的转换器
     * @see com.squareup.moshi.JsonAdapter.failOnUnknown
     * @return MoshiPayloadConvertFactory
     */
    fun failOnUnknown() = MoshiConvertFactory(moshi, lenient, true, serializeNulls)
    /**
     * 创建在编码JSON时序列化空值的转换器
     * @see com.squareup.moshi.JsonAdapter.serializeNulls
     * @return MoshiPayloadConvertFactory
     */
    fun withNullSerialization() = MoshiConvertFactory(moshi, lenient, failOnUnknown, true)

    override fun getConverter(targetType: Class<*>): Converter<String, *> {
        var adapter = moshi.adapter(targetType)
        if(lenient) adapter = adapter.lenient()
        if(failOnUnknown) adapter = adapter.failOnUnknown()
        if(serializeNulls) adapter = adapter.serializeNulls()
        return MoshiPayloadConverter(adapter)
    }
}