package com.gitee.xuankaicat.kmnkt.mqtt.enhance.convert

interface Converter<F, T> {
    fun convert(from: F): T?

    interface Factory<F, T> {
        fun getConverter(targetType: Class<*>): Converter<F, *>
    }
}