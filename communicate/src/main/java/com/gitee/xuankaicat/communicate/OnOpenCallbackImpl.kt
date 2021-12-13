package com.gitee.xuankaicat.communicate

import android.util.Log

/**
 * OnOpenCallback的默认实现
 */
open class OnOpenCallbackImpl : OnOpenCallback {
    private var success: ((Communicate) -> Unit) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接成功")
    }

    private var failure: ((Communicate) -> Boolean) = { communicate ->
        Log.v("openCallback", "${communicate.address}:建立连接失败")
        Thread.sleep(5000)
        Log.v("openCallback", "正在尝试重新连接...")
        true
    }

    fun success(method: (communicate: Communicate) -> Unit) {
        success = method
    }

    fun failure(method: (communicate: Communicate) -> Boolean) {
        failure = method
    }

    override fun success(communicate: Communicate) {
        success.invoke(communicate)
    }

    override fun failure(communicate: Communicate): Boolean {
        return failure.invoke(communicate)
    }
}