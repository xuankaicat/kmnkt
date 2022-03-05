@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

interface IOnOpenCallback {
    /**
     * 打开成功回调
     * @param ISocket 连接对象
     */
    fun success(ISocket: ISocket)

    /**
     * 打开失败回调
     * @param ISocket 连接对象
     * @return 重新尝试连接
     */
    fun failure(ISocket: ISocket): Boolean

    /**
     * 失去连接回调
     * @param ISocket Communicate
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    fun loss(ISocket: ISocket): Boolean
}