package com.gitee.xuankaicat.kmnkt.socket.utils

actual object Log : ILog {
    override fun v(tag: String, msg: String): Int {
        println("V/${tag}: $msg")
        return -1
    }

    override fun d(tag: String, msg: String): Int {
        println("\u001B[32mD/${tag}: ${msg}\u001B[0m")
        return -1
    }

    override fun i(tag: String, msg: String): Int {
        println("\u001B[34mI/${tag}: ${msg}\u001B[0m")
        return -1
    }

    override fun w(tag: String, msg: String): Int {
        println("\u001B[33mW/${tag}: ${msg}\u001B[0m")
        return -1
    }

    override fun e(tag: String, msg: String): Int {
        System.err.println("E/${tag}: $msg")
        return -1
    }
}