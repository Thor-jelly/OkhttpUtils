package com.jelly.thor.example.model

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/1/31 12:01 <br/>
 */
data class PagerModel(
    val pageSize: Int,
    val list: List<PagerCModel>
)
data class PagerCModel(
    val pageIndex: Int,
    val name: String
)
