package com.jelly.thor.okhttputils.utils

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * 类描述：string 扩展<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/4/19 19:26 <br/>
 */
internal fun String?.parseInt(): Int {
    if (this.isNullOrEmpty()) {
        return 0
    }
    return try {
        this.toInt()
    } catch (e: Exception) {
        //Log.w("123===", this + "-->String转换Int异常=" + e.message)
        this.formatNumber(0).parseInt()
    }
}

internal fun String?.parseDouble(): Double {
    if (this.isNullOrEmpty()) {
        return 0.0
    }
    return try {
        this.toDouble()
    } catch (e: Exception) {
        //Log.w("123===", this + "-->String转换Int异常=" + e.message)
        0.0
    }
}

internal fun String?.parseBoolean(): Boolean {
    if (this.isNullOrEmpty()) {
        return false
    }
    return try {
        this.toBoolean()
    } catch (e: Exception) {
        //Log.w("123===", this + "-->String转换Int异常=" + e.message)
        false
    }
}

/**
 * 格式化数字升级版
 * @param dot 保留小数点位数
 * @param isShowEnd0 是否显示末尾0
 * @param roundingMode 四舍五入模式
 */
@JvmOverloads
internal fun String?.formatNumber(
    dot: Int,
    isShowEnd0: Boolean = false,
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): String {
    val patternSb = StringBuilder("0")
    for (i in 0 until dot) {
        if (i == 0) {
            patternSb.append(".")
        }
        if (isShowEnd0) {
            patternSb.append("0")
        } else {
            patternSb.append("#")
        }
    }
    return this.formatNumber(patternSb.toString(), roundingMode)
}

/**
 * 格式化数字
 * ##0.000
 * @param roundingMode 四舍五出模式
 */
@JvmOverloads
internal fun String?.formatNumber(
    formatStr: String = "#,##0.####",
    roundingMode: RoundingMode = RoundingMode.HALF_UP
): String {
    val df = DecimalFormat(formatStr)
    df.roundingMode = roundingMode
    if (this.isNullOrEmpty()) {
        return df.format(0)
    }
    val numberD: Double
    try {
        numberD = this.toDouble()
    } catch (e: Exception) {
        return df.format(0)
    }
    return df.format(numberD)
}

/**
 * 格式化金额，默认直接格式化后台返回数据
 * @param isNeedComma 是否需要逗号
 * @param isShowEnd0 显示末尾0个数，如果设置就不根据后台返回格式化小数位
 */
@JvmOverloads
internal fun String?.formatNumberPrice(
    isNeedComma: Boolean = true,
    isShowEnd0: Int = -1
): String {
    if (this.isNullOrEmpty()) {
        return if (isShowEnd0 == -1) {
            "0.00"
        } else if (isShowEnd0 == 0) {
            "0"
        } else if (isShowEnd0 > 0) {
            val sb = StringBuilder("0.")
            for (i in 0 until isShowEnd0) {
                sb.append("0")
            }
            sb.toString()
        } else {
            "0.00"
        }
    }

    val patternSb = StringBuilder(if (isNeedComma) "#,##0" else "##0")
    if (isShowEnd0 == -1) {
        patternSb.append(".")
        if (this.contains(".")) {
            var end0 = this.split(".")[1].length
            if (end0 < 2) {
                end0 = 2
            }
            for (i in 0 until end0) {
                patternSb.append("0")
            }
        } else {
            patternSb.append("00##")
        }
    } else if (isShowEnd0 > 0) {
        patternSb.append(".")
        for (i in 0 until isShowEnd0) {
            patternSb.append("0")
        }
    }

    val df = DecimalFormat(patternSb.toString())
    df.roundingMode = RoundingMode.HALF_UP
    val numberD: Double
    try {
        numberD = this.toDouble()
    } catch (e: Exception) {
        return df.format(0)
    }
    return df.format(numberD)
}