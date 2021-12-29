@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.nio.charset.Charset
import kotlin.concurrent.thread

class TCP : Communicate {
    private var socket: Socket? = null
    override var serverPort = 9000
    private var _address: InetAddress = InetAddress.getByName("192.168.200.1")
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

    override fun send(message: String) {
        thread {
            try {
                output?.write(message.toByteArray(outCharset))
            } catch (e: Exception) {
                Log.e("TCP", "发送信息失败，可能是网络连接问题 {uri: '${address}', port: ${serverPort}}")
                e.printStackTrace()
            }
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(receiveThread != null) return false
        isReceiving = true
        val receive = ByteArray(100)
        receiveThread = thread {
            while (isReceiving) {
                try {
                    Log.v("TCP", "开始接收消息 {uri: '${address}', port: ${serverPort}}")
                    val len = input?.read(receive) ?: 0
                    if(len != 0) {
                        Handler(Looper.getMainLooper()).post {
                            onReceive(String(receive, 0, len, inCharset), receive)
                        }
                    }
                } catch (ignore: Exception) {
                    Log.v("TCP", "停止接收消息 {uri: '${address}', port: ${serverPort}}")
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
        var success = false
        thread {
            do {
                try {
                    Log.v("TCP", "开始尝试建立连接 {uri: '${address}', port: ${serverPort}}")
                    socket = Socket(address, serverPort)
                    if(socket?.keepAlive == true) {
                        onOpenCallback.success(this)
                        success = true
                        Log.v("TCP", "建立连接成功 {uri: '${address}', port: ${serverPort}}")
                    }
                } catch (e: Exception) {
                    Log.e("TCP", "建立连接失败 {uri: '${address}', port: ${serverPort}}")
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