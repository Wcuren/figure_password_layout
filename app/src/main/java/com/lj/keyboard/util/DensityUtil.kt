package com.lj.keyboard.util

import android.content.Context

/**
 * 包含静态函数的工具类
 */
object DensityUtil {

    fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.getResources().getDisplayMetrics().density
        return (dpValue*scale + 0.5f).toInt()
    }

}