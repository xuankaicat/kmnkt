package com.gitee.xuankaicat.kmnkt.socket.utils

interface ILog {
    fun v(tag: String, msg: String): Int
    fun d(tag: String, msg: String): Int
    fun i(tag: String, msg: String): Int
    fun w(tag: String, msg: String): Int
    fun e(tag: String, msg: String): Int
}

expect object LogImpl : ILog

object EmptyLog : ILog {
    override fun v(tag: String, msg: String): Int = -1

    override fun d(tag: String, msg: String): Int = -1

    override fun i(tag: String, msg: String): Int = -1

    override fun w(tag: String, msg: String): Int = -1

    override fun e(tag: String, msg: String): Int = -1

}

interface ILoggable {
    /**
     * 启用默认日志
     * 默认为true
     */
    var enableDefaultLog: Boolean

    @Suppress("PropertyName")
    val Log
        get() = if(enableDefaultLog) LogImpl else EmptyLog
}