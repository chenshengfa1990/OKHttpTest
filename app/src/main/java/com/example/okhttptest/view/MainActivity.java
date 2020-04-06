package com.example.okhttptest.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.okhttptest.R;
import com.example.okhttptest.presenter.BaiduNetdiskPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends Activity implements IBaiduNetDiskView{

    private static final int DOWNPICTURE = 0;
    private static final int PRECREATE = 1;
    private Context mContext;
    private BaiduNetdiskPresenter mPresenter;

    private String baiduAccessToken;

    @BindView(R.id.textview)
    public TextView tv;

    @BindView(R.id.textview1)
    public TextView tv1;

    private Handler handler = new Handler() {


        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mPresenter = new BaiduNetdiskPresenter(this, mContext);
        mPresenter.getBaiduAccessToken();

        ButterKnife.bind(this);
        tv.setText("Chinese");
        tv1.setText("English");
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("csf", "begin");
                mPresenter.OkHttpTest(baiduAccessToken);
            }
        });

        Button downButton = (Button) findViewById(R.id.button2);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("csf", "download start");
                mPresenter.download(baiduAccessToken);
            }
        });

        Button uploadButton = (Button) findViewById(R.id.button3);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                mPresenter.download(baiduAccessToken);
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 || PackageManager.PERMISSION_GRANTED != grantResults[0]) {
            Toast.makeText(this,"你拒绝了权限，无法创建!",Toast.LENGTH_LONG).show();
        }else {
        }
    }



    @Override
    public void showAccessToken(String accessToken) {
        baiduAccessToken = accessToken;
        Log.d("csf", baiduAccessToken);
    }

    @Override
    public void showOkHttpTestResult(String respone) {
        Log.d("csf", respone);
    }

    @Override
    public void showDownloadResult(String respone) {
        Log.d("csf", respone);
    }

    @Override
    public void showUploadResult(String respone) {
        Log.d("csf", respone);
    }
}
