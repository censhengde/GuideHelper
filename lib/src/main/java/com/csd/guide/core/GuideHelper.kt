package com.qq.ac.android.guide

import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.annotation.IdRes
import androidx.core.util.containsKey
import androidx.core.util.isNotEmpty
import androidx.core.util.valueIterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.csd.guide.core.isOpenLog
import java.io.Serializable
import java.util.ArrayList

/**

 * Author：岑胜德 on 2021/11/8 10:38

 * 说明：

 */
class GuideHelper private constructor(
    private var parent: View? = null,
    private val activity: FragmentActivity? = null,
    private val fragment: Fragment? = null
) : Serializable {

    companion object {
        internal const val KEY_ANCHOR_INFO_LIST = "KEY_ANCHOR_INFO_LIST"

        @JvmStatic
        fun with(activity: FragmentActivity? = null, fragment: Fragment? = null, parent: View? = null): GuideHelper {
            return GuideHelper(parent, activity, fragment)
        }

        @JvmStatic
        fun openLog(open: Boolean) {
            isOpenLog = open
        }
    }

    init {
        parent = activity?.window?.decorView
        parent = fragment?.view
    }

    private val anchorBridgeMap by lazy(LazyThreadSafetyMode.NONE) {
        SparseArray<AnchorBridge>()
    }
    private var dialog: BaseGuideDialogFragment? = null

    fun addAnchor(@IdRes viewId: Int): GuideHelper {
        parent?.apply {
            val anchor = this.findViewById<View>(viewId)
            addAnchor(anchor)
        }
        return this
    }

    fun addAnchor(anchor: View): GuideHelper {
        val key = anchor.hashCode()
        if (!this.anchorBridgeMap.containsKey(key)) {
            val bridge = AnchorBridge(anchor, dialog)
            anchorBridgeMap.put(key, bridge)
        }
        return this
    }


    fun attach(dialog: BaseGuideDialogFragment): GuideHelper {
        this.dialog = dialog
        return this
    }

    fun show(args: Bundle? = null, tag: String? = null): Boolean {
        val fm = activity?.supportFragmentManager ?: fragment?.childFragmentManager
        ?: throw RuntimeException("请先调用 with（）方法，且必须传入 Activity、Fragment之一！")

        if (anchorBridgeMap.isNotEmpty()) {
            val info = ArrayList<AnchorInfo>(anchorBridgeMap.size())
            anchorBridgeMap.valueIterator().forEach {
                info.add(it.anchorInfo)
            }
            dialog?.onAttachAnchorInfo(info)
        }
        args?.apply {
            dialog?.arguments = this
        }
        return dialog?.showSafely(fm, tag) ?: false

    }

}