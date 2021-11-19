package com.qq.ac.android.guide

import android.view.View
import android.view.ViewTreeObserver
import com.csd.guide.core.logI

/**

 * Author：岑胜德 on 2021/11/11 10:45

 * 说明：
 * 锚点连接器：将 Anchor 与 OnAnchorScreenLocationChangedListener（实现者：BaseGuideDialogFragment） 关联起来，
 *           当 Anchor 发生坐标变化，则回调新坐标信息给 OnAnchorScreenLocationChangedListener。
 */
class AnchorBridge(private val anchor: View, private val listener: OnAnchorLocationListener?) {
    val anchorInfo = AnchorInfo(anchor)
    private val location = IntArray(2)

    init {
        tryToUpdateAnchorFrame()
        val vto = anchor.viewTreeObserver
        vto.addOnWindowAttachListener(object : ViewTreeObserver.OnWindowAttachListener {
            override fun onWindowAttached() {
                "===> onWindowAttached ".logI(this@AnchorBridge)
                tryToUpdateAnchorFrame()
            }

            override fun onWindowDetached() {

            }
        })
        anchor.rootView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            "===> onLayoutChange ".logI(this)
            tryToUpdateAnchorFrame()
        }
    }

    private fun tryToUpdateAnchorFrame() {
        if (anchor.isAttachedToWindow) {
            anchor.getLocationOnScreen(location)
            // 检查锚点的坐标是否发生变化
            var changed = false
            val frame = anchorInfo.frame
            val l = location[0]
            if (frame.left != l) {
                frame.left = l
                changed = true
            }

            val t = location[1]
            if (frame.top != t) {
                frame.top = t
                changed = true
            }

            val r = l + anchor.width
            if (frame.right != r) {
                frame.right = r
                changed = true
            }
            val b = t + anchor.height
            if (frame.bottom != b) {
                frame.bottom = b
                changed = true
            }
            if (changed) {
                listener?.onLocationChanged(anchorInfo)
            }
        }
    }
}