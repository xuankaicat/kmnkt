package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Thread

/**
 * 支持带端口的发送
 */
interface ISendWithPort {
    /**
     * 发送数据
     * @param port 发送端口
     * @param message 数据内容
     */
    fun send(port: Int, message: String)

    /**
     * 发送数据
     * @param port 发送端口
     * @param message 数据内容
     * @param times 发送次数，-1表示持续发送
     * @param delay 发送间隔，单位为毫秒
     * @return 执行发送数据的线程
     * > 想要在发送期间停止发送数据可以对返回的线程对象调用`interrupt()`函数
     */
    fun send(port: Int , message: String, times: Int, delay: Long): Thread
}