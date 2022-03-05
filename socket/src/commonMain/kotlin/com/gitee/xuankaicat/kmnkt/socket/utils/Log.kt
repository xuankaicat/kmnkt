package com.gitee.xuankaicat.kmnkt.socket.utils

interface ILog {
    fun v(tag: String, msg: String): Int
    fun d(tag: String, msg: String): Int
    fun i(tag: String, msg: String): Int
    fun w(tag: String, msg: String): Int
    fun e(tag: String, msg: String): Int
}

expect object Log : ILog