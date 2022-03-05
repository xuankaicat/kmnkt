package com.gitee.xuankaicat.kmnkt.socket

import com.gitee.xuankaicat.kmnkt.socket.utils.Log

/**
 * OnOpenCallback的默认实现
 */
open class OnOpenCallback : IOnOpenCallback {
    private var success: ((Communicate) -> Unit) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接成功")
    }

    private var failure: ((Communicate) -> Boolean) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接失败，等待5秒后尝试重新连接")
        Thread.sleep(5000)
        Log.v("openCallback", "${communicate.address}:尝试重新连接...")
        true
    }

    private var loss: ((Communicate) -> Boolean) = { communicate ->
        Log.v("openCallback", "${communicate.address}:失去连接，尝试重新连接...")
        true
    }

    /**
     * 打开成功回调
     * @param method 连接成功回调
     */
    fun success(method: (communicate: Communicate) -> Unit) {
        success = method
    }

    /**
     * 打开失败回调
     * @param method 连接失败回调
     * @return 重新尝试连接
     */
    fun failure(method: (communicate: Communicate) -> Boolean) {
        failure = method
    }

    /**
     * 失去连接回调
     * @param method 失去连接回调
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    fun loss(method: (communicate: Communicate) -> Boolean) {
        loss = method
    }

    override fun success(communicate: Communicate) {
        success.invoke(communicate)
    }

    override fun failure(communicate: Communicate): Boolean {
        return failure.invoke(communicate)
    }

    override fun loss(communicate: Communicate): Boolean {
        return loss.invoke(communicate)
    }
}