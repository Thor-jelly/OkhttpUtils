package com.example.example;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.builder.PostFileBuilder;
import com.example.okhttputils.callback.Callback;
import com.example.okhttputils.callback.FileCallback;
import com.example.okhttputils.request.OkHttpRequest;
import com.example.okhttputils.tag.TagBeen;
import com.example.okhttputils.utils.LoadDialogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "123===";
    private TextView mGetTv;
    private TextView mPostTv;
    private TextView mSocketTv;
    private WebSocket mSocket;
    private TextView mSocketCancelTv;
    private TextView mSocketAgainTv;
    private TextView mFileUplodeTv;
    private TextView mDownTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGetTv = (TextView) findViewById(R.id.get_tv);
        mPostTv = (TextView) findViewById(R.id.post_tv);
        mSocketTv = (TextView) findViewById(R.id.socket_tv);
        mSocketCancelTv = (TextView) findViewById(R.id.socket_cancel_tv);
        mSocketAgainTv = (TextView) findViewById(R.id.socket_again_tv);
        mFileUplodeTv = (TextView) findViewById(R.id.file_uplode_tv);
        mDownTv = (TextView) findViewById(R.id.down_tv);

        initEvent();
    }

    private void initEvent() {
        mGetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMethod();
                getMethod();
                getMethod();
                getMethod();
            }
        });


        mPostTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMethod();
            }
        });

        mSocketTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketMethod();
            }
        });

        mSocketCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketCancelMethod();
            }
        });

        mSocketAgainTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketAgainMethod();
            }
        });

        mFileUplodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadeMethod();
            }
        });

        mDownTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downFileMethod();
            }
        });

    }

    private void downFileMethod() {
//        v5.ishandian.com.cn/shop/upload/download?format=json
        Map<String, String> map = new HashMap<>();
        map.put("fileName", "IMG_20180517_100948.jpg");
        OkHttpUtils.get()
                .url("http://v5.ishandian.com.cn/shop/upload/download?format=json")
                .params(map)
                .build()
                .execute(new FileCallback(Environment.getExternalStorageDirectory().getAbsolutePath(), "DCIM/Camera/abcccc.jpg") {
                    @Override
                    public void onError(int code, String errorMessage, int id, OkHttpRequest okHttpRequest) {

                    }

                    @Override
                    public void onResponse(int code, File response, int id) {

                    }
                });
    }

    private void uploadeMethod() {
        File downloadCacheDirectory = Environment.getDownloadCacheDirectory();
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String absolutePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        File file = new File(Environment.getExternalStorageDirectory(),"DCIM/Camera/IMG_20180517_100948.jpg");
        if (!file.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        List<PostFileBuilder.FileInput> fileInputList = new ArrayList<>();
        PostFileBuilder.FileInput fileInput = new PostFileBuilder.FileInput("file", "IMG_20180517_100948.jpg", file);
        fileInputList.add(fileInput);
        OkHttpUtils
                .postFile()
                .url("http://v5.ishandian.com.cn/shop/upload/upload?format=json")
                .files(fileInputList)
                .build()
                .execute(getCallback());
    }

    private void socketAgainMethod() {
        if (mSocket != null) {
            //socketMethod();
            //socket关闭了再发送就没有效果只能重新创建再发送
            //mSocket.send("{\"cmd\":\"login\",\"roomId\":\"10010\",\"token\":\"10013-bj0Yl0s7dl0iTglCZ9HYJcP0VaHsq5Ls\"}");
            //Log.d(TAG, "发送数据");
        }
    }

    private void socketCancelMethod() {
        if (mSocket != null) {
            mSocket.close(1000, "手动关闭");
        }
    }

    private void socketMethod() {
        OkHttpUtils.WebSocket()
                .url("wss://ws.shandian.net:8082")
                .newBuild()
                .execute(new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        mSocket = webSocket;
                        mSocket.send("{\"cmd\":\"login\",\"roomId\":\"10010\",\"token\":\"10013-SMIu3beRBxs4u17M63CY97rTc2TFZVEH\"}");
                        Log.d(TAG, "onOpen: 连接成功!");
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);
                        Log.d(TAG, "onMessage: 接收到数据=" + text);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        super.onMessage(webSocket, bytes);
                        Log.d(TAG, "onMessage: 接收到数据=" + bytes.utf8());
                    }



                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                        super.onClosing(webSocket, code, reason);
                        Log.d(TAG, "onClosing: 正要准备关闭->code:" + code + " --reason:" + reason);
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        super.onClosed(webSocket, code, reason);
                        Log.d(TAG, "onClosed: 关闭->code:" + code + " --reason:" + reason.toString());
                        Log.d(TAG, "onClosed: 1->" + (mSocket == null));
                        Log.d(TAG, "onClosed: 2->" + (webSocket == null));
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                        super.onFailure(webSocket, t, response);
                        Log.d(TAG, "onFailure: 失败->Throwable:" + t.getMessage());
                        Log.d(TAG, "onFailure: 1->" + (mSocket == null));
                        Log.d(TAG, "onFailure: 2->" + (webSocket == null));
                    }
                });
    }

    private void postMethod() {
        //https://b.shandian.net/shop/entry/login?format=json&token=&sdSig=b2134ecdfe26ec6a563b3b410abdfebe&sdTime=1526439268
        //mobile=13817975415&password=123456&loginType=0


        //
        Map<String, String> map = new HashMap<>();
        map.put("mobile", "13817975415");
        map.put("password", "123456");
        map.put("loginType", "0");
        OkHttpUtils.post()
                .url("http://v5.qa.ishandian.com.cn/shop/entry/login?format=json&token=10013-WCzY5b3qY5CHBjIN4jQCYAt31fI1qNFc&sdSig=b2134ecdfe26ec6a563b3b410abdfebe&sdTime=1526439268")
                .params(map)
                .build()
                .execute(getCallback());
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
    int getId = 0;
    private void getMethod() {
        getId++;
        OkHttpUtils.get()
                .id(getId)
//                .url("order/showOrderDetail?&oid=201806190030000011&shopId=10010&isPay=1&token=10013-XCUWLwRtZzSjfkodQH0NL4GpyeFiAVQn&format=json")
                .url("https://b.shandian.net/pos/order/showOrderDetail?&oid=201806190030000011&shopId=10010&isPay=1&token=10013-XCUWLwRtZzSjfkodQH0NL4GpyeFiAVQn&format=json")
                .isShowDialog(true)
                .tag(MainActivity.class)
                .build()
                .execute(getCallback());
    }

    @NonNull
    private Callback getCallback() {
        return new Callback() {
            @Override
            public void onError(int code, String errorMessage, int id, OkHttpRequest okHttpRequest) {
                Log.d(TAG, "onError: code=" + code + "  errorMessage=" + errorMessage);
            }

            @Override
            public void onResponse(int code, Object response, int id) {
                Log.d(TAG, "onResponse: id=" + id + "  response=" + (response == null));
            }

            @Override
            public Object parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception {
                Log.d(TAG, "parseNetworkResponse: id=" + id + "  response=" + response.body().string());
                return null;
            }

            @Override
            public void onAfter(int id) {
                super.onAfter(id);
            }
        };
    }
}
