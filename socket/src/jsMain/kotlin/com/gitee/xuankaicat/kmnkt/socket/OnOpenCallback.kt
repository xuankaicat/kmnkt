package com.gitee.xuankaicat.kmnkt.socket

/**
 * OnOpenCallback的默认实现
 */
actual open class OnOpenCallback : IOnOpenCallback {
    override fun success(ISocket: ISocket) {
        TODO("Not yet implemented")
    }

    override fun failure(ISocket: ISocket): Boolean {
        TODO("Not yet implemented")
    }

    override fun loss(ISocket: ISocket): Boolean {
        TODO("Not yet implemented")
    }
}