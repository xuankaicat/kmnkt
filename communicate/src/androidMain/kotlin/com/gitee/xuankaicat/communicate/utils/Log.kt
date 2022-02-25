package com.gitee.xuankaicat.communicate.utils

import android.util.Log as androidLog

actual object Log: ILog {
    override fun v(tag: String, msg: String) = androidLog.v(tag, msg)
    override fun d(tag: String, msg: String) = androidLog.d(tag, msg)
    override fun i(tag: String, msg: String) = androidLog.i(tag, msg)
    override fun w(tag: String, msg: String) = androidLog.w(tag, msg)
    override fun e(tag: String, msg: String) = androidLog.e(tag, msg)
}