package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Log

/**
 * OnOpenCallback的默认实现
 */
open class OnOpenCallback : IOnOpenCallback {
    private var success: ((ISocket) -> Unit) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接成功")
    }

    private var failure: ((ISocket) -> Boolean) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接失败，等待5秒后尝试重新连接")
        Thread.sleep(5000)
        Log.v("openCallback", "${communicate.address}:尝试重新连接...")
        true
    }

    private var loss: ((ISocket) -> Boolean) = { communicate ->
        Log.v("openCallback", "${communicate.address}:失去连接，尝试重新连接...")
        true
    }

    /**
     * 打开成功回调
     * @param method 连接成功回调
     */
    fun success(method: (ISocket: ISocket) -> Unit) {
        success = method
    }

    /**
     * 打开失败回调
     * @param method 连接失败回调
     * @return 重新尝试连接
     */
    fun failure(method: (ISocket: ISocket) -> Boolean) {
        failure = method
    }

    /**
     * 失去连接回调
     * @param method 失去连接回调
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    fun loss(method: (ISocket: ISocket) -> Boolean) {
        loss = method
    }

    override fun success(ISocket: ISocket) {
        success.invoke(ISocket)
    }

    override fun failure(ISocket: ISocket): Boolean {
        return failure.invoke(ISocket)
    }

    override fun loss(ISocket: ISocket): Boolean {
        return loss.invoke(ISocket)
    }
}