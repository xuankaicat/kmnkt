@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Thread
import com.gitee.xuankaicat.kmnkt.socket.utils.mainThread
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset
import kotlin.concurrent.thread

actual open class UDP : ISocket, ISendWithPort {
    override var enableDefaultLog = true

    private var _socket: DatagramSocket? = null
    override val socket: Any?
        get() = _socket

    override var port: Int = 9000
    private var _address: InetAddress = InetAddress.getByName("10.0.2.2")
    override var address: String
        get() = _address.hostAddress!!
        set(value) {
            _address = InetAddress.getByName(value)
        }

    override var callbackOnMain: Boolean = true

    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false
    private var receiveThread: Thread? = null

    @Deprecated(
        message = "udp中port由原先的发送端口改为了接收端口，请使用支持带端口发送的send方法替代该方法",
        replaceWith = ReplaceWith("send(orderPort, message)"),
        level = DeprecationLevel.WARNING
    )
    override fun send(message: String) = send(port, message)

    @Deprecated(
        message = "udp中port由原先的发送端口改为了接收端口，请使用支持带端口发送的send方法替代该方法",
        replaceWith = ReplaceWith("send(orderPort, message, times, delay)"),
        level = DeprecationLevel.WARNING
    )
    override fun send(message: String, times: Int, delay: Long): Thread = send(port, message, times, delay)

    override fun send(port: Int, message: String) {
        if(_socket == null) return
        val bytes = message.toByteArray(outCharset)
        val len = bytes.size
        val sendPacket = DatagramPacket(bytes, len, _address, port)
        thread {
            doSend(port, sendPacket)
        }
    }

    override fun send(port: Int, message: String, times: Int, delay: Long) = thread {
        if(_socket == null) return@thread
        val bytes = message.toByteArray(outCharset)
        val len = bytes.size
        val sendPacket = DatagramPacket(bytes, len, _address, port)
        var nowTimes = times
        Log.v("UDP", "开始循环发送信息,剩余次数: $nowTimes, 间隔: $delay {uri: '${address}', port: ${port}}")
        while (nowTimes != 0) {
            thread {
                doSend(port, sendPacket)
            }
            Thread.sleep(delay)
            if(nowTimes > 0) nowTimes--
        }
    }

    private fun doSend(port: Int, sendPacket: DatagramPacket) {
        try {
            _socket?.send(sendPacket)
        } catch (e: Exception) {
            Log.e("UDP", "发送信息失败，可能是网络连接问题 {uri: '${address}', port: ${port}}")
            e.printStackTrace()
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(_socket == null) return false
        if(receiveThread != null) return false
        isReceiving = true
        val receive = ByteArray(100)
        val receivePacket = DatagramPacket(receive, receive.size)
        receiveThread = thread {
            while (isReceiving) {
                for (i in 0 until 100) {
                    if(receive[i].compareTo(0) == 0) break
                    receive[i] = 0
                }

                try {
                    Log.v("UDP", "开始接收消息 {uri: '${address}', port: ${port}}")
                    _socket?.receive(receivePacket)
                    val data = String(receivePacket.data, inCharset)
                    val ip = receivePacket.address.hostAddress!!
                    if (callbackOnMain) {
                        mainThread {
                            isReceiving = onReceive(data, ip)
                        }
                    } else {
                        isReceiving = onReceive(data, ip)
                    }
                } catch (ignore: Exception) {
                    Log.v("UDP", "停止接收消息 {uri: '${address}', port: ${port}}")
                    break
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

    override fun open(onOpenCallback: IOnOpenCallback): UDP {
        _socket = DatagramSocket(port)
        onOpenCallback.success(this)
        return this
    }

    override fun close() {
        _socket?.close()
        _socket = null
    }
}