@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.ILoggable

/**
 * OnOpenCallback的默认实现
 */
actual open class OnOpenCallback actual constructor(
    loggable: ILoggable
) : IOnOpenCallback {

    private val log = loggable.Log

    private var success: ((ISocket) -> Unit) = { socket ->
        log.v("openCallback", "${socket.address}:建立连接成功")
    }

    private var failure: ((ISocket) -> Boolean) = { socket ->
        log.v("openCallback", "${socket.address}:建立连接失败，等待5秒后尝试重新连接")
        Thread.sleep(5000)
        log.v("openCallback", "${socket.address}:尝试重新连接...")
        true
    }

    private var loss: ((ISocket) -> Boolean) = { socket ->
        log.v("openCallback", "${socket.address}:失去连接，尝试重新连接...")
        true
    }

    private var error: ((ISocket, Throwable) -> Unit) = { socket, throwable ->
        throwable.printStackTrace()
        throw throwable
    }

    /**
     * 打开成功回调
     * @param method 连接成功回调
     */
    actual fun success(method: (socket: ISocket) -> Unit) {
        success = method
    }

    /**
     * 打开失败回调
     * @param method 连接失败回调
     * @return 重新尝试连接
     */
    actual fun failure(method: (socket: ISocket) -> Boolean) {
        failure = method
    }

    /**
     * 失去连接回调
     * @param method 失去连接回调
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    actual fun loss(method: (socket: ISocket) -> Boolean) {
        loss = method
    }

    actual fun error(method: (socket: ISocket, throwable: Throwable) -> Unit) {
        error = method
    }

    override fun success(socket: ISocket) {
        success.invoke(socket)
    }

    override fun failure(socket: ISocket): Boolean {
        return failure.invoke(socket)
    }

    override fun loss(socket: ISocket): Boolean {
        return loss.invoke(socket)
    }

    override fun error(socket: ISocket, throwable: Throwable) {
        return error.invoke(socket, throwable)
    }
}