package com.jelly.thor.example

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jelly.thor.example.netserver.PostApi
import com.jelly.thor.example.utils.autoDispose2MainE
import com.jelly.thor.okhttputils.OkHttpUtils
import com.jelly.thor.okhttputils.builder.PostFileBuilder.FileInput
import com.jelly.thor.okhttputils.callback.Callback
import com.jelly.thor.okhttputils.callback.FileCallback
import com.jelly.thor.okhttputils.request.OkHttpRequest
import okhttp3.*
import okio.ByteString
import okio.ByteString.Companion.decodeHex
import java.io.File

class MainActivity : AppCompatActivity() {
    private var mGetTv: TextView? = null
    private var mPostTv: TextView? = null
    private var mSocketTv: TextView? = null
    private var mSocket: WebSocket? = null
    private var mSocketCancelTv: TextView? = null
    private var mSocketAgainTv: TextView? = null
    private var mFileUplodeTv: TextView? = null
    private var mDownTv: TextView? = null
    private var mPostStringTv: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGetTv = findViewById<View>(R.id.get_tv) as TextView
        mPostTv = findViewById<View>(R.id.post_tv) as TextView
        mPostStringTv = findViewById<View>(R.id.post_string_tv) as TextView
        mSocketTv = findViewById<View>(R.id.socket_tv) as TextView
        mSocketCancelTv = findViewById<View>(R.id.socket_cancel_tv) as TextView
        mSocketAgainTv = findViewById<View>(R.id.socket_again_tv) as TextView
        mFileUplodeTv = findViewById<View>(R.id.file_uplode_tv) as TextView
        mDownTv = findViewById<View>(R.id.down_tv) as TextView
        initEvent()
    }

    private fun initEvent() {
        mGetTv!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                method
            }
        })
        mPostTv!!.setOnClickListener { postMethod() }
        mPostStringTv!!.setOnClickListener { postStringMethod() }
        mSocketTv!!.setOnClickListener {
            socketMethod()
            //testSocketMethod();
        }
        mSocketCancelTv!!.setOnClickListener { socketCancelMethod() }
        mSocketAgainTv!!.setOnClickListener { socketAgainMethod() }
        mFileUplodeTv!!.setOnClickListener { uploadeMethod() }
        mDownTv!!.setOnClickListener { downFileMethod() }
    }

    private fun downFileMethod() {
//        v5.ishandian.com.cn/shop/upload/download?format=json
        val map: MutableMap<String, String> = HashMap()
        map["fileName"] = "IMG_20180517_100948.jpg"
        OkHttpUtils.get()
            .url("http://v5.ishandian.com.cn/shop/upload/download?format=json")
            .params(map)
            .build()
            .execute(object : FileCallback("abcccc.jpg") {
                override fun onError(
                    code: Int,
                    errorMessage: String,
                    id: Int,
                    okHttpRequest: OkHttpRequest
                ) {
                }

                override fun onResponse(code: Int, response: Uri, id: Int) {}
            })
    }

    private fun uploadeMethod() {
        val downloadCacheDirectory = Environment.getDownloadCacheDirectory()
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        val absolutePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val file =
            File(Environment.getExternalStorageDirectory(), "DCIM/Camera/IMG_20180517_100948.jpg")
        if (!file.exists()) {
            Toast.makeText(this@MainActivity, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show()
            return
        }
        val fileInputList: MutableList<FileInput> = ArrayList()
        val fileInput = FileInput("file", "IMG_20180517_100948.jpg", file)
        fileInputList.add(fileInput)
        OkHttpUtils
            .postFile()
            .url("http://v5.ishandian.com.cn/shop/upload/upload?format=json")
            .files(fileInputList)
            .build()
            .execute<Any>(callback)
    }

    private fun socketAgainMethod() {
        if (mSocket != null) {
            //socketMethod();
            //socket关闭了再发送就没有效果只能重新创建再发送
            //mSocket.send("{\"cmd\":\"login\",\"roomId\":\"10010\",\"token\":\"10013-bj0Yl0s7dl0iTglCZ9HYJcP0VaHsq5Ls\"}");
            //Log.d(TAG, "发送数据");
        }
    }

    private fun socketCancelMethod() {
        if (mSocket != null) {
            mSocket!!.close(1000, "手动关闭")
        }
    }

    /**
     * 测试关闭接口是否有回调的
     */
    private fun testSocketMethod() {
        val request: Request = Request.Builder().url("ws://echo.websocket.org").build()
        //        Request request = new Request.Builder().url("wss://ws.shandian.net:8082").build();
        val client = OkHttpClient()
        val ws = client.newWebSocket(request, object : WebSocketListener() {
            private val NORMAL_CLOSURE_STATUS = 1000
            override fun onOpen(webSocket: WebSocket, response: Response) {
//                webSocket.send("{\"cmd\":\"login\",\"roomId\":\"10010\",\"token\":\"10013-gG6NP4myIFHcvwsB9h6UBABMH5HvM4P0\"}");
                webSocket.send("Hello, it's SSaurel !")
                webSocket.send("What's up ?")
                webSocket.send("deadbeef".decodeHex())
                webSocket.close(NORMAL_CLOSURE_STATUS, "我是谁 !")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Receiving : $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d(TAG, "Receiving bytes : " + bytes.hex())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                //webSocket.close(NORMAL_CLOSURE_STATUS, null);
                Log.d(TAG, "Closing : $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "onClosed : $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d(TAG, "Error : " + t.message)
            }
        })
        client.dispatcher.executorService.shutdown()
    }

    private fun socketMethod() {
        OkHttpUtils.WebSocket()
            .url("wss://echo.websocket.org") //                .url("wss://ws.shandian.net:8082")
            .newBuild()
            .execute(object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    mSocket = webSocket
                    mSocket!!.send("{\"cmd\":\"login\",\"roomId\":\"10010\",\"token\":\"10013-SMIu3beRBxs4u17M63CY97rTc2TFZVEH\"}")
                    Log.d(TAG, "onOpen: 连接成功!")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    Log.d(TAG, "onMessage: 接收到数据=$text")
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                    Log.d(TAG, "onMessage: 接收到数据=" + bytes.utf8())
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosing(webSocket, code, reason)
                    Log.d(TAG, "onClosing: 正要准备关闭->code:$code --reason:$reason")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    Log.d(TAG, "onClosed: 关闭->code:$code --reason:$reason")
                    Log.d(TAG, "onClosed: 1->" + (mSocket == null))
                    Log.d(TAG, "onClosed: 2->" + (webSocket == null))
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    Log.d(TAG, "onFailure: 失败->Throwable:" + t.message)
                    Log.d(TAG, "onFailure: 1->" + (mSocket == null))
                    Log.d(TAG, "onFailure: 2->" + (webSocket == null))
                }
            })
    }

    private fun postStringMethod() {
        //gson解析对数据要求较高 请自定义数据类型
        /* PostApi.postStringRxJavaTest().autoDispose2MainE(this)
             .subscribe({
                 Log.d(TAG, "1="+it.toString())
             }, {

             })*/

        /* PostApi.postStringResponseStr().autoDispose2MainE(this)
             .subscribe({
                 Log.d(TAG, "2="+it)
             }, {

             })*/

        /* PostApi.postStringResponse().autoDispose2MainE(this)
             .subscribe({
                 Log.d(TAG, "3=" + it)
             }, {

             })*/

        /* PostApi.postStringOkhttpResponse().autoDispose2MainE(this)
             .subscribe({
                 Log.d(TAG,"4="+ it.body?.string() ?: "")
             }, {

             })*/
/*
        PostApi.postStringList().autoDispose2MainE(this)
            .subscribe({
                Log.d(TAG,"5="+ it.toString() ?: "")
            }, {

            })*/

        /*PostApi.postStringPager().autoDispose2MainE(this)
            .subscribe({
                Log.d(TAG,"6="+ it.toString() ?: "")
            }, {

            })*/

        PostApi.postStringPagerResponse().autoDispose2MainE(this)
            .subscribe({
                Log.d(TAG, "7=" + it.toString() ?: "")
            }, {

            })

//        val map: MutableMap<String, String> = HashMap()
//        map["mobile"] = "13811112222"
//        map["password"] = "123456"
//        map["loginType"] = "0"
//        OkHttpUtils.postString()
//            .url("http://v5.qa.ishandian.com.cn/shop/entry/login?token=10013-WCzY5b3qY5CHBjIN4jQCYAt31fI1qNFc&sdSig=b2134ecdfe26ec6a563b3b410abdfebe&sdTime=1526439268")
//            .addParam("scannerGunCode", "44a159c5cfc6696")
//            .addParam("action", 2.toString())
//            .build()
//            .execute(object : Callback<Any?>() {
//                override fun onResponse(code: Int, response: Any?, id: Int) {
//
//                }
//                @Throws(Exception::class)
//                override fun parseNetworkResponse(
//                    response: Response,
//                    id: Int,
//                    okHttpRequest: OkHttpRequest
//                ): Any? {
//                    return null
//                }
//            })
    }

    private fun postMethod() {
        //https://b.shandian.net/shop/entry/login?format=json&token=&sdSig=b2134ecdfe26ec6a563b3b410abdfebe&sdTime=1526439268
        //mobile=13811112222&password=123456&loginType=0
        //
        val map: MutableMap<String, String> = HashMap()
        map["mobile"] = "13811112222"
        map["password"] = "123456"
        map["loginType"] = "0"
        OkHttpUtils.post()
            .url("http://v5.qa.ishandian.com.cn/shop/entry/login?token=10013-WCzY5b3qY5CHBjIN4jQCYAt31fI1qNFc&sdSig=b2134ecdfe26ec6a563b3b410abdfebe&sdTime=1526439268")
            .params(map)
            .build()
            .execute<Any>(callback)
    }

    //    private void getMethod() {
    //        Map<String, String> map = new HashMap<>();
    //        map.put("shopId", "10189");
    //        map.put("tableVer", "1526380979");
    //        map.put("otherConfigVer", "1526379063");
    //        map.put("goodsConfigVer", "1526270091");
    //        OkHttpUtils.get()
    //                .url("http://v5.qa.ishandian.com.cn/pos/order/showOrderList?" +
    //                        "format=json" +
    //                        "&token=10013-d5bJJcAi9HIHWJLvUlfNRUMTo7WcVsmY")
    //                .params(map)
    //                .isShowDialog(true)
    //                .build()
    //                .execute(getCallback());
    //    }
    private var getId = 0

    //                .url("https://b.shandian.net/pos/order/showOrderDetail?&oid=201806190030000011&shopId=10010&isPay=1&token=10013-XCUWLwRtZzSjfkodQH0NL4GpyeFiAVQn&format=json")
    private val method: Unit
        get() {
            getId++
            OkHttpUtils.get()
                .id(getId)
                .url("http://v5.qa.ishandian.com.cn/pos/order/showOrderDetail?&oid=201806190030000011&shopId=10010&isPay=1&token=10013-XCUWLwRtZzSjfkodQH0NL4GpyeFiAVQn&format=json") //                .url("https://b.shandian.net/pos/order/showOrderDetail?&oid=201806190030000011&shopId=10010&isPay=1&token=10013-XCUWLwRtZzSjfkodQH0NL4GpyeFiAVQn&format=json")
                .isShowDialog(true)
                .tag(MainActivity::class.java)
                .build()
                .execute<Any>(callback)
        }

    private val callback: Callback<Any?>
        get() = object : Callback<Any?>() {
            override fun onError(
                code: Int,
                errorMessage: String,
                id: Int,
                okHttpRequest: OkHttpRequest
            ) {
                Log.d(TAG, "onError: code=$code  errorMessage=$errorMessage")
            }

            override fun onResponse(code: Int, response: Any?, id: Int) {
                Log.d(TAG, "onResponse: id=" + id + "  response=" + (response == null))
            }

            @Throws(Exception::class)
            override fun parseNetworkResponse(
                response: Response,
                id: Int,
                okHttpRequest: OkHttpRequest
            ): Any? {
                Log.d(
                    TAG,
                    "parseNetworkResponse: id=" + id + "  response=" + response.body!!.string()
                )
                return null
            }

            override fun onAfter(id: Int) {
                super.onAfter(id)
            }
        }

    companion object {
        private const val TAG = "123==="
    }
}