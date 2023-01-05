@file:Suppress("unused")

package com.gitee.xuankaicat.kmnkt.socket

interface IOnOpenCallback {
    /**
     * 打开成功回调
     * @param socket 连接对象
     */
    fun success(socket: ISocket)

    /**
     * 打开失败回调
     * @param socket 连接对象
     * @return 重新尝试连接
     */
    fun failure(socket: ISocket): Boolean

    /**
     * 失去连接回调
     * @param socket Communicate
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    fun loss(socket: ISocket): Boolean
}