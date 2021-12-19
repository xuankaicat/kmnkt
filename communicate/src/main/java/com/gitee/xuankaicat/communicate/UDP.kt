@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.os.Handler
import android.os.Looper
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.charset.Charset
import kotlin.concurrent.thread

class UDP : Communicate {
    private var socket: DatagramSocket? = null
    override var serverPort = 9000
    private var _address: InetAddress = InetAddress.getByName("192.168.200.1")
    override var address: String
        get() = _address.hostAddress!!
        set(value) {
            _address = InetAddress.getByName(value)
        }
    override var inCharset: Charset = Charsets.UTF_8
    override var outCharset: Charset = Charsets.UTF_8

    private var isReceiving = false
    private var receiveThread: Thread? = null

    override fun send(message: String) {
        if(socket == null) return
        val bytes = message.toByteArray(outCharset)
        val len = bytes.size
        val sendPacket = DatagramPacket(bytes, len, _address, serverPort)
        thread {
            socket?.send(sendPacket)
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(socket == null) return false
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
                    socket?.receive(receivePacket)
                    val data = String(receivePacket.data, inCharset)
                    val ip = receivePacket.address.hostAddress!!
                    Handler(Looper.getMainLooper()).post {
                        isReceiving = onReceive(data, ip)
                    }
                } catch (ignore: Exception) {
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

    override fun open(onOpenCallback: IOnOpenCallback) {
        socket = DatagramSocket()
        onOpenCallback.success(this)
    }

    override fun close() {
        socket?.close()
        socket = null
    }
}