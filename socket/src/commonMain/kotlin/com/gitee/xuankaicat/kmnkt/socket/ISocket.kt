@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Charset
import com.gitee.xuankaicat.kmnkt.socket.utils.ILoggable
import com.gitee.xuankaicat.kmnkt.socket.utils.Thread
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

typealias OnReceiveFunc = (String, Any) -> Boolean
typealias OnReceiveSimpleFunc = (String) -> Boolean

interface ISocket : ILoggable {
    companion object {
        @JvmStatic
        val TCPClient: TCP
            @JvmName("TCPClient")
            get() = TCP()

        /**
         * 构造TCPClient
         * - 在创建时返回该对象会尝试开启连接
         * @param build 构造lambda
         * @return TCPClient
         */
        @JvmStatic
        fun getTCPClient(build: ISocket.() -> ISocket?): ISocket = TCPClient.apply {
            build(this)?.open()
        }

        @JvmStatic
        val UDP: UDP
            @JvmName("UDP")
            get() = UDP()

        /**
         * 构造UDP
         * - 在创建时返回该对象会尝试开启连接
         * @param build 构造lambda
         * @return UDP
         */
        @JvmStatic
        fun getUDP(build: ISocket.() -> ISocket?): ISocket = UDP.apply {
            build(this)?.open()
        }
    }

    /**
     * 通信对象，如果通信未打开则为null
     * - 对于UDP，对象类型为DatagramSocket?
     * - 对于TCPClient，对象类型为Socket?
     * - 对于MQTT，对象类型为MqttClient?
     * - 对MQTT对象类型的操作需要引入`org.eclipse.paho:org.eclipse.paho.client.mqttv3`依赖
     */
    val socket: Any?

    /**
     * 自身通信端口
     */
    var port: Int

    /**
     * 目标通信地址
     */
    var address: String

    /**
     * 在主线程上执行回调
     */
    var callbackOnMain: Boolean

    /**
     * 输入编码
     */
    var inCharset: Charset

    /**
     * 输出编码
     */
    var outCharset: Charset

    /**
     * 发送数据
     * @param message 数据内容
     */
    fun send(message: String)

    /**
     * 发送数据
     * @param message 数据内容
     * @param times 发送次数，-1表示持续发送
     * @param delay 发送间隔，单位为毫秒
     * @return 执行发送数据的线程
     * > 想要在发送期间停止发送数据可以对返回的线程对象调用`interrupt()`函数
     */
    fun send(message: String, times: Int, delay: Long): Thread

    /**
     * 开始接收数据
     * @param onReceive 处理接收到的数据的函数，函数返回值为是否继续接收消息.
     * 请不要在函数中使用stopReceive()函数停止接收数据，这不会起作用。
     * @return 是否开启成功
     */
    fun startReceive(onReceive: OnReceiveFunc): Boolean

    /**
     * 停止接收数据
     */
    fun stopReceive()

    /**
     * 开启通信，用于TCP与MQTT建立连接
     */
    fun open() = open(OnOpenCallback(this))

    /**
     * 开启通信，用于TCP与MQTT建立连接
     * @param onOpenCallback 开启成功或失败的回调，默认失败会等待5秒重新尝试连接。
     */
    //@JsName("openWith") 升级至1.7.20后报错，目前也用不到js，先注释掉
    fun open(onOpenCallback: IOnOpenCallback): ISocket

    /**
     * 关闭通信
     */
    fun close()
}

/**
 * 开始接收数据
 * @param onReceive 处理接收到的数据的函数，函数返回值为是否继续接收消息.
 * 请不要在函数中使用stopReceive()函数停止接收数据，这不会起作用。
 * @return 是否开启成功
 */
inline fun ISocket.startReceive(crossinline onReceive: OnReceiveSimpleFunc): Boolean =
    this.startReceive { v, _ -> onReceive.invoke(v) }


/**
 * 使用DSL构建开始通信
 * @receiver ISocket 连接对象
 * @param callback 开启成功或失败的回调，默认失败会等待5秒重新尝试连接。
 */
inline fun ISocket.open(callback: OnOpenCallback.() -> Unit)
    = open(OnOpenCallback(this).also(callback))

/**
 * 等待开启完成的开启通信，用于TCP与MQTT建立连接
 */
fun ISocket.openSync() = openSync(OnOpenCallback(this))

/**
 * 等待开启完成的开启通信，用于TCP与MQTT建立连接
 * @param onOpenCallback 开启成功或失败的回调，默认失败会等待5秒重新尝试连接。
 */
fun ISocket.openSync(onOpenCallback: IOnOpenCallback) {
    runBlocking {
        var finish = false
        open(object : OnOpenCallback(this@openSync) {
            override fun success(socket: ISocket) {
                onOpenCallback.success(socket)
                finish = true
            }

            override fun failure(socket: ISocket): Boolean {
                return onOpenCallback.failure(socket)
            }

            override fun loss(socket: ISocket): Boolean {
                return onOpenCallback.loss(socket)
            }
        })
        while (!finish) delay(50L)
    }
}

/**
 * 使用DSL构建等待开启完成的开始通信
 * @receiver ISocket 连接对象
 * @param callback 开启成功或失败的回调，默认失败会等待5秒重新尝试连接。
 */
inline fun ISocket.openSync(callback: OnOpenCallback.() -> Unit)
    = openSync(OnOpenCallback(this).also(callback))