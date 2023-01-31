package com.jelly.thor.example.model

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/1/30 15:35 <br/>
 */
data class RxJavaTestModel(
    val id: String,
    val list: List<RxJavaTestCModel>
)
data class RxJavaTestCModel(
    val age: String,
    val name: String
)
