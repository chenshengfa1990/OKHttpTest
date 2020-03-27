package com.example.okhttptest.model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.api.AccessTokenManager;
import com.baidu.api.Baidu;
import com.baidu.api.BaiduDialog;
import com.baidu.api.BaiduDialogError;
import com.baidu.api.BaiduException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiduNetdiskModel {

    private static final int DOWNPICTURE = 0;
    private static final int PRECREATE = 1;
    private Baidu baidu;
    private String clientId = "1tF3aayFmy2t22vMIrfYwH60";
    private String mAccessToken = null;
    private Context mContext;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNPICTURE:
                    String dlink = (String)msg.obj;
                    Log.d("csf", dlink);

                    HttpUrl.Builder urlBuilder = HttpUrl.parse(dlink).newBuilder();
                    urlBuilder.addQueryParameter("access_token", mAccessToken);

                    OkHttpClient client = new OkHttpClient();
                    String userAgent = System.getProperty("http.agent");
                    Request request = new Request.Builder().url(urlBuilder.build()).addHeader("User-Agent", userAgent).build();
                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //Log.d("csf", "download fail");
                            Log.d("csf", e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("csf", "download success");
                            //Log.d("csf", response.body().string());
                            writeFile(response);
                        }
                    });
                    break;

                case PRECREATE:
                    Response precreate = (Response) msg.obj;
                    String precreateStr = null;
                    try {
                        precreateStr = precreate.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("csf", precreateStr);

                    String uploadPath = null;
                    String uploadid = null;
                    String request_id = null;
                    try {
                        JSONObject object = new JSONObject(precreateStr);
                        uploadPath = object.getString("path");
                        uploadid = object.getString("uploadid");
                        request_id = object.getString("request_id");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String url = "https://d.pcs.baidu.com/rest/2.0/pcs/superfile2";
                    HttpUrl.Builder urlBuilder1 = HttpUrl.parse(url).newBuilder();
                    urlBuilder1.addQueryParameter("access_token", mAccessToken)
                            .addQueryParameter("method", "upload")
                            .addQueryParameter("type", "tmpfile")
                            .addQueryParameter("path", uploadPath)
                            .addQueryParameter("uploadid", uploadid)
                            .addQueryParameter("partseq", "0");

                    OkHttpClient client1 = new OkHttpClient();
                    RequestBody body = new FormBody.Builder()
                            .add("file", uploadPath)
                            .build();
                    Request request1 = new Request.Builder().post(body).url(urlBuilder1.build()).build();
                    client1.newCall(request1).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("csf", "precreate fail");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("csf", "precreate success");
                            Log.d("csf", response.body().string());
                        }
                    });
                    break;
            }
        }
    };

    public BaiduNetdiskModel(Context context) {
        mContext = context;
    }

    public void getBaiduAccessToken(final IAccessTokenListener listener) {
        baidu = new Baidu(clientId, mContext);
        baidu.authorize((Activity) mContext, false,true, new BaiduDialog.BaiduDialogListener() {
            @Override
            public void onComplete(Bundle bundle) {
                baidu.init(mContext);
                AccessTokenManager atm = baidu.getAccessTokenManager();
                String accessToken = atm.getAccessToken();
                mAccessToken = accessToken;

                listener.accessTokenResult(accessToken);
            }

            @Override
            public void onBaiduException(BaiduException e) {

            }

            @Override
            public void onError(BaiduDialogError baiduDialogError) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void OkHttpTest(String accessToken, final IResponeListener listener) {
        //        String url = "https://www.baidu.com";
        //String url = "https://www.huobi.vc/zh-cn/";
        //String url = "https://www.huobi.vc/";
        //String url = "https://api.huobi.de.com/v1/common/symbols";//所有交易对
        //String url = "https://status.huobigroup.com/api/v2/summary.json";
        //String url = "https://api.zb.live/data/v1/allTicker";
        //String url = "https://api.zb.live/data/v1/ticker?market=btc_usdt";
        //String url = "https://api.zb.live/data/v1/kline?market=btc_usdt";
        //String url = "https://api.douban.com/v2/book/isbn/:9787303152971";
        //String url = "https://api.huobi.de.com/v1/common/currencys";//所有币种
        //String url = "https://api.huobi.de.com/v1/common/timestamp";//时间戳
//                String url = "https://api.huobi.de.com/market/history/kline?period=1day&size=200&symbol=btcusdt";
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://openapi.baidu.com/oauth/2.0/authorize?response_type=token&client_id=1tF3aayFmy2t22vMIrfYwH60&redirect_uri=bdconnect://success&scope=basic,netdisk&display=popup&state=xxx")));
        //String url = "https://openapi.baidu.com/oauth/2.0/authorize?response_type=token&client_id=1tF3aayFmy2t22vMIrfYwH60&redirect_uri=oob&scope=basic,netdisk&display=popup&state=xxx";
        String url = "https://pan.baidu.com/rest/2.0/xpan/nas?method=uinfo";
        //String url = "https://openapi.baidu.com/rest/2.0/passport/users/getInfo";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("access_token", accessToken);

        File httpCacheDirectory = new File(mContext.getCacheDir(), "responses");
        Log.d("csf", "cache directory: " + httpCacheDirectory.getAbsolutePath());
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new NetCacheInterceptor())
                .cache(cache)
                .build();
        String userAgent = System.getProperty("http.agent");
        Request request = new Request.Builder().url(urlBuilder.build()).addHeader("User-Agent", userAgent).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("csf", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("csf", "hhh");
                //Log.d("csf", response.message());
                //Log.d("csf", response.body().string());
                String body = response.body().string();
                listener.OnResponeBody(body);
            }
        });
    }

    public void download(String accessToken, IResponeListener listener) {
        String url = "https://pan.baidu.com/rest/2.0/xpan/multimedia?method=filemetas";
        //String downloadUrl = "https://d.pcs.baidu.com/file/25f1f019edf80c9e4e38be88df84e664?fid=1544055245-250528-1001512175410777\\u0026rt=pr\\u0026sign=FDtAERV-DCb740ccc5511e5e8fedcff06b081203-7jdO54MUPAZMdVnG8K2olfUeBBM%3D\\u0026expires=8h\\u0026chkbd=0\\u0026chkv=2\\u0026dp-logid=3122090307104449323\\u0026dp-callid=0\\u0026dstime=1584702354\\u0026r=849209308";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("access_token", accessToken)
                .addQueryParameter("fsids", "[1001512175410777]")
                .addQueryParameter("dlink", "1");

        OkHttpClient client = new OkHttpClient();
        String userAgent = System.getProperty("http.agent");
        Request request = new Request.Builder().url(urlBuilder.build()).addHeader("User-Agent", userAgent).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("csf", response.message().toString());
                //Log.d("csf", response.body().string());
                String jsonStr = new String(response.body().string());
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    if(object.has("list")) {
                        JSONArray jsonArray = object.getJSONArray("list");
                        JSONObject subObject = (JSONObject) jsonArray.get(0);
                        if(subObject.has("dlink")) {
                            String dlink = subObject.getString("dlink");
                            //Log.d("csf", dlink);
                            Message msg = Message.obtain();
                            msg.what = DOWNPICTURE;
                            msg.obj = dlink;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void writeFile(Response response) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
        }

        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File[] sdPath = mContext.getExternalFilesDirs(Environment.MEDIA_MOUNTED);
        //String path = this.getExternalFilesDir(null).getAbsolutePath();
        String path = sdPath[1].getAbsolutePath();


        File filePath = new File(path);
        if(!filePath.exists()) {
            filePath.mkdirs();
        }
        File file = new File(filePath, "dou.heic");
        Log.d("csf", file.getAbsolutePath());

        InputStream is = null;
        FileOutputStream fos = null;
        is = response.body().byteStream();
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            //获取下载的文件的大小
            long fileSize = response.body().contentLength();
            long sum = 0;
            int porSize = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes);
                //sum += len;
                //porSize = (int) ((sum * 1.0f / fileSize) * 100);
                //Message message = handler.obtainMessage(1);
                //message.arg1 = porSize;
                //handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload(String accessToken, final IResponeListener listener) {
        String url = "https://pan.baidu.com/rest/2.0/xpan/file?method=precreate";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("access_token", accessToken);

        OkHttpClient client = new OkHttpClient();
        String filePath = mContext.getExternalFilesDir(null).getAbsolutePath();

        File file = new File(filePath, "test.jpg");
        Log.d("csf", "file path: " + file.getAbsolutePath());
        if(file.exists()) {
            Log.d("csf", "file size is " + file.length());
        }

        String md5Str = getFileMD5s(file, 32);
        JSONArray md5Json = new JSONArray();
        md5Json.put(md5Str);


        RequestBody body = new FormBody.Builder()
                .add("path", file.getAbsolutePath())
                .add("size", file.length() + "")
                .add("isdir", "0")
                .add("autoinit", "1")
                .add("block_list", md5Json.toString())
                .build();

        String userAgent = System.getProperty("http.agent");
        Request request = new Request.Builder().post(body).url(urlBuilder.build()).addHeader("User-Agent", userAgent).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Log.d("csf", response.body().string());
//                Message msg = Message.obtain();
//                Bundle bundle = new Bundle();
//                bundle.put
//                msg.what = PRECREATE;
//                msg.obj = response;
//                handler.sendMessage(msg);
                String result = response.body().string();
                listener.OnResponeBody(result);
            }
        });
    }

    public static String getFileMD5s(File file,int radix) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(radix);
    }

}
