@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import com.gitee.xuankaicat.communicate.utils.Log
import com.gitee.xuankaicat.communicate.utils.mainThread
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import kotlin.concurrent.thread

class TCP : Communicate {
    private var socket: Socket? = null
    override var port: Int = 9000
    private var _address: InetAddress = InetAddress.getByName("10.0.2.2")
    override var address: String
        get() = _address.hostAddress!!
        set(value) {
            _address = InetAddress.getByName(value)
        }
    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    val input: InputStream?
        get() = socket?.getInputStream()
    val output: OutputStream?
        get() = socket?.getOutputStream()

    private var isReceiving = false
    private var receiveThread: Thread? = null
    private var onOpenCallback: IOnOpenCallback = OnOpenCallback()

    override fun send(message: String) {
        thread {
            try {
                output?.write(message.toByteArray(outCharset))
            } catch (e: Exception) {
                Log.e("TCP", "发送信息失败，可能是网络连接问题 {uri: '${address}', port: ${port}}")
                e.printStackTrace()
            }
        }
    }

    override fun send(message: String, times: Int, delay: Long): Thread = thread {
        var nowTimes = times
        Log.v("TCP", "开始循环发送信息,剩余次数: $nowTimes, 间隔: $delay {uri: '${address}', port: ${port}}")
        while (nowTimes != 0) {
            send(message)
            Thread.sleep(delay)
            if(nowTimes > 0) nowTimes--
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(receiveThread != null) return false
        isReceiving = true
        val receive = ByteArray(100)
        receiveThread = thread {
            while (isReceiving) {
                try {
                    Log.v("TCP", "开始接收消息 {uri: '${address}', port: ${port}}")
                    val len = input?.read(receive) ?: 0
                    if(len != 0) {
                        mainThread {
                            onReceive(String(receive, 0, len, inCharset), receive)
                        }
                    }
                } catch (ignore: Exception) {
                    if(socket?.isConnected == true) {
                        //stopReceive
                        Log.v("TCP", "停止接收消息 {uri: '${address}', port: ${port}}")
                        break
                    } else {
                        //连接异常中断
                        if(onOpenCallback.loss(this)) {
                            //重新连接
                            doConnection()
                        }
                    }
                }
            }
            isReceiving = false
            receiveThread = null
        }
        return true
    }

    override fun stopReceive() {
        receiveThread?.interrupt()
    }

    override fun open(onOpenCallback: IOnOpenCallback) {
        //存储回调对象
        this.onOpenCallback = onOpenCallback
        //初始化连接对象
        try {
            socket = Socket(address, port)
        } catch (e: Exception) {
            Log.e("TCP", "创建Socket失败 {uri: '${address}', port: ${port}}")
            e.printStackTrace()
            return
        }
        //执行连接
        doConnection()
    }

    private fun doConnection() {
        thread {
            var success = false
            do {
                try {
                    Log.v("TCP", "开始尝试建立连接 {uri: '${address}', port: ${port}}")
                    if(socket?.keepAlive == true) {
                        onOpenCallback.success(this)
                        success = true
                        Log.v("TCP", "建立连接成功 {uri: '${address}', port: ${port}}")
                    }
                } catch (e: Exception) {
                    Log.e("TCP", "建立连接失败 {uri: '${address}', port: ${port}}")
                    e.printStackTrace()
                } finally {
                    if(!success) success = !onOpenCallback.failure(this)
                }
            } while (!success)
        }
    }

    override fun close() {
        socket?.close()
    }
}