@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

import android.os.Handler
import android.os.Looper
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
            output?.write(message.toByteArray(outCharset))
        }
    }

    override fun startReceive(onReceive: OnReceiveFunc): Boolean {
        if(receiveThread != null) return false
        isReceiving = true
        val receive = ByteArray(100)
        receiveThread = thread {
            while (isReceiving) {
                try {
                    val len = input?.read(receive) ?: 0
                    if(len != 0) {
                        Handler(Looper.getMainLooper()).post {
                            onReceive(String(receive, 0, len, inCharset))
                        }
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

    override fun open(onOpenCallback: OnOpenCallback) {
        var success = false
        thread {
            do {
                try {
                    socket = Socket(address, serverPort)
                    if(socket?.keepAlive == true) {
                        onOpenCallback.success(this)
                        success = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if(!success) success = onOpenCallback.failure(this)
                }
            } while (!success)
        }
    }

    override fun close() {
        socket?.close()
    }
}