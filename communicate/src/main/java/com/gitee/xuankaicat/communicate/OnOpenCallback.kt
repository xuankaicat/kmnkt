@file:Suppress("unused")

package com.gitee.xuankaicat.communicate

interface OnOpenCallback {
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

}