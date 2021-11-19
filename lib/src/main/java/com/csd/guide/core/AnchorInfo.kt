package com.qq.ac.android.guide

import android.graphics.Rect
import android.view.View
import java.io.Serializable

/**

 * Author：岑胜德 on 2021/11/10 11:42

 * 说明：锚点 View 信息包装类。目前包括的信息有：
 *       1.锚点四个顶点的 Windows 坐标。
 *      2. 锚点本身。

 */
class AnchorInfo(val anchor: View) : Serializable {
    // 锚点四个顶点的 Windows 坐标。
    val frame: Rect by lazy(LazyThreadSafetyMode.NONE) {
        Rect()
    }
    val isNotEmpty: Boolean
        get() = !frame.isEmpty

    override fun toString(): String {
        return "AnchorInfo(frame=$frame)"
    }

}