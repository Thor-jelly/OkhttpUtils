package com.jelly.thor.example.netserver

import com.google.gson.Gson


/**
 * 类描述：Json 请求参数工具类<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/5/5 15:49 <br/>
 */
class JsonParamsUtils private constructor() {
    companion object {
        private val fromModel2StringGson = Gson()

        @JvmStatic
        @JvmOverloads
        fun builder(type: JsonParamsTypeEnum = JsonParamsTypeEnum.COMMON): JsonParamsUtils {
            return when (type) {
                JsonParamsTypeEnum.COMMON -> {
                    val j = JsonParamsUtils()
                    j.addCommonParams()
                    j
                }
                else -> JsonParamsUtils()
            }
        }
    }

    private var paramsHp: HashMap<String, Any>? = null
    private var dataParamsHp: HashMap<String, Any>? = null

    //初始化添加的一些参数,如果是通过toModel添加参数方式 初始化参数JsonRequestModel中添加
    private fun initParamsHp() {
        if (this.paramsHp == null) {
            this.paramsHp = LinkedHashMap()
        }
        if (this.dataParamsHp == null) {
            this.dataParamsHp = LinkedHashMap()
        }
    }

    /**
     * 传递的是model，是data model
     */
    fun addDataModel(value: Any?): JsonParamsUtils {
        initParamsHp()
        if (value != null) {
            this.paramsHp!!["data"] = value
        }
        return this
    }

    /**
     * 添加data params
     */
    fun addDataParam(key: String, value: Any?): JsonParamsUtils {
        initParamsHp()
        if (value != null) {
            this.dataParamsHp!![key] = value
        }
        return this
    }

    /**
     * 添加data params
     */
    fun addDataParams(params: HashMap<String, out Any>): JsonParamsUtils {
        initParamsHp()
        this.dataParamsHp!!.putAll(params)
        return this
    }

    /**
     * 添加params
     */
    fun addParam(key: String, value: Any?): JsonParamsUtils {
        initParamsHp()
        if (value != null) {
            this.paramsHp!![key] = value
        }
        return this
    }

    /**
     * 添加params
     */
    fun addParams(params: HashMap<String, out Any>): JsonParamsUtils {
        initParamsHp()
        this.paramsHp!!.putAll(params)
        return this
    }

//    /**
//     * 添加分页参数
//     */
//    fun addPageParam(current: Int, pageSize: Int = 50, key: String = "page"): JsonParamsUtils {
//        initParamsHp()
//        this.paramsHp!![key] = PageRequest(current, pageSize)
//        return this
//    }

    fun build(): String {
        if (dataParamsHp == null) {
            initParamsHp()
        }
        if (!dataParamsHp.isNullOrEmpty()) {
            this.paramsHp?.put("data", dataParamsHp!!)
        }
        if (!this.paramsHp!!.containsKey("data")) {
            this.paramsHp!!["data"] = EmptyData()
        }
        return fromModel2StringGson.toJson(paramsHp)
    }

    /**
     * 添加公用参数
     * 添加uid coid
     */
    private fun addCommonParams(): JsonParamsUtils {
        initParamsHp()
        this.paramsHp!!["uid"] = "UserInfoManager.getUId()"
        this.paramsHp!!["coid"] = "UserInfoManager.getUCoId()"
        return this
    }
}