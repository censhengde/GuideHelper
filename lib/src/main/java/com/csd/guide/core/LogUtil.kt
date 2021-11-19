package com.csd.guide.core

import android.os.Build
import android.util.Log

/**

 * Author：岑胜德 on 2021/11/19 10:19

 * 说明：

 */
internal var isOpenLog = false

internal fun String.logI(tagObj: Any) {
    if (isOpenLog) {
        Log.i(tagObj.javaClass.simpleName, this)
    }
}
internal fun String.logE(tagObj: Any) {
    if (isOpenLog) {
        Log.e(tagObj.javaClass.simpleName, this)
    }
}