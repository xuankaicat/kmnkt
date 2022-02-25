@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

interface IOnOpenCallback {
    /**
     * 打开成功回调
     * @param communicate 连接对象
     */
    fun success(communicate: Communicate)

    /**
     * 打开失败回调
     * @param communicate 连接对象
     * @return 重新尝试连接
     */
    fun failure(communicate: Communicate): Boolean

    /**
     * 失去连接回调
     * @param communicate Communicate
     * @return 重新尝试连接
     * > 尝试重新连接后的连接成功与失败将会触发success与failure回调
     */
    fun loss(communicate: Communicate): Boolean
}