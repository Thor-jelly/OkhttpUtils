package com.jelly.thor.example.netserver

/**
 * 类描述：域名相关 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/5/9 14:54 <br/>
 */
class ApiUrlConstant private constructor() {
    private class HOLDER {
        companion object {
            internal val H = ApiUrlConstant()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): ApiUrlConstant {
            return HOLDER.H
        }

        //第一个api 域名规则
        @JvmStatic
        val FIRST_API
            get() = if (true)  {
                getInstance().DEV
            } else {
                getInstance().RELEASE
            }

        //第一个api 域名规则
        @JvmStatic
        val SECOND_API
            get() = if (true)  {
                getInstance().OLD_API_DEV
            } else {
                getInstance().OLD_API_RELEASE
            }

        fun getApiUrl(afterUrl: String): String {
            //拼接规则
            //以"webapi/jht/api"匹配 --- 直接域名 + swagger url
            //--- 直接域名 + swagger url
            return if (afterUrl.startsWith("webapi/ddw/api")) {
                "${FIRST_API}${afterUrl}"
            } else if (afterUrl.startsWith("/webapi/ddw/api")) {
                //防止小伙伴传递afterUrl 开始多写了”/“
                "${FIRST_API}${afterUrl.substring(1)}"
            } else if (afterUrl.startsWith("/")) {
                "${SECOND_API}${afterUrl.substring(1)}"
            } else {
                "${SECOND_API}${afterUrl}"
            }
        }
    }

    /////////////////////////First-API///////////////////////////////
    val DEV = "http://dev-api.first.com/"
    val RELEASE = "http://api.first.com/"
    /////////////////////////First-API-END///////////////////////////////

    ////////////////////////seconde-API//////////////
    val OLD_API_DEV = "http://dev-ap.second.com"
    val OLD_API_RELEASE = "http://api.second.com"
    ////////////////////////seconde-API///////////////////////////////
}