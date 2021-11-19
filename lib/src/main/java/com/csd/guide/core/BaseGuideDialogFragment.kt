package com.qq.ac.android.guide

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.csd.guide.core.logE
import com.csd.guide.core.logI

/**

 * Author：岑胜德 on 2021/11/5 10:56

 * 说明：整个新手引导的技术方案核心思想就是：拿到目标View（anchor view） 的屏幕坐标、宽高，
 *      然后再根据这些数据确定Dialog中的引导布局怎么摆放。(参考 PopupWindow 实现原理）
 *      注意事项：1.要求 BaseGuideDialogFragment 的根视图坐标系必须与 anchor view的坐标系一致；
 *               2.一次 anchor 坐标变化对应一次 onAlignEachGuideLayout 方法回调。

 */
abstract class BaseGuideDialogFragment : DialogFragment(), OnAnchorLocationListener {

    private var anchorInfoList: List<AnchorInfo>? = null

    fun onAttachAnchorInfo(info: List<AnchorInfo>) {
        anchorInfoList = info
    }

    @LayoutRes
    protected abstract fun getContentLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContentLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anchorInfoList?.forEach {
            if (it.isNotEmpty) {
                onAlignEachGuideLayout(it)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        dialog?.window?.decorView?.systemUiVisibility = option
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
        if (Build.VERSION.SDK_INT >= 21) {
            dialog.window?.addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )
        }
        val lp = dialog.window?.attributes
        // 适配 28版本的刘海屏
        if (Build.VERSION.SDK_INT >= 28) {
            lp?.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            dialog.window?.attributes = lp
        }
        return dialog
    }

    override fun onLocationChanged(anchorInfo: AnchorInfo) {
        "===> onLocationChanged $anchorInfo view：$view".logI(this)
        view ?: return // 要求 dialog 视图必须创建完成
        if (anchorInfo.frame.isEmpty) {
            return
        }
        onAlignEachGuideLayout(anchorInfo)
    }

    /**
     * 根据提供的 anchor view 所在屏幕的位置信息决定怎么布置引导布局。
     * @param anchorInfo 锚点信息。
     */
    protected abstract fun onAlignEachGuideLayout(anchorInfo: AnchorInfo)

    open fun showSafely(fm: FragmentManager, tag: String? = null): Boolean {
        var success = true
        try {
            // 有可能因为宿主 activity/fragment状态问题引起异常.
            this.show(fm, tag)
        } catch (e: Exception) {
            e.message?.logE(this)
            success = false
        }
        return success
    }


}