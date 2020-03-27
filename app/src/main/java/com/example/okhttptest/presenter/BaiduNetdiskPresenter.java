package com.example.okhttptest.presenter;

import android.content.Context;

import com.example.okhttptest.model.BaiduNetdiskModel;
import com.example.okhttptest.model.IAccessTokenListener;
import com.example.okhttptest.model.IResponeListener;
import com.example.okhttptest.view.IBaiduNetDiskView;

public class BaiduNetdiskPresenter {
    private Context mContext;
    private IBaiduNetDiskView baiduNetDiskView;
    private BaiduNetdiskModel baiduNetDiskModel;
    public BaiduNetdiskPresenter(IBaiduNetDiskView view, Context context) {
        baiduNetDiskView = view;
        mContext = context;
        baiduNetDiskModel = new BaiduNetdiskModel(context);
    }

    public void getBaiduAccessToken() {
        baiduNetDiskModel.getBaiduAccessToken(new IAccessTokenListener() {
            @Override
            public void accessTokenResult(String result) {
                baiduNetDiskView.showAccessToken(result);
            }
        });
    }

    public void OkHttpTest(String accessToken) {
        baiduNetDiskModel.OkHttpTest(accessToken, new IResponeListener() {
            @Override
            public void OnResponeBody(String body) {
                baiduNetDiskView.showOkHttpTestResult(body);
            }
        });
    }

    public void download(String accessToken) {
        baiduNetDiskModel.download(accessToken, new IResponeListener() {
            @Override
            public void OnResponeBody(String body) {
                baiduNetDiskView.showDownloadResult(body);
            }
        });
    }

    public void upload(String accessToken) {
        baiduNetDiskModel.download(accessToken, new IResponeListener() {
            @Override
            public void OnResponeBody(String body) {
                baiduNetDiskView.showUploadResult(body);
            }
        });
    }
}
