package com.module.questionnaire.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.module.questionnaire.R;
import com.module.questionnaire.utils.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class WebViewActivity extends AppCompatActivity {

    private ContentLoadingProgressBar mProgressBar;
    private WebView mWebView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initData();
        initWebView();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.web_view_progress_clpb);
        mWebView = findViewById(R.id.web_view_wv);
    }

    private void initData() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url").replace("web_present/", "");
    }

    private void initWebView() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);

        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setBuiltInZoomControls(false);//support zoom

        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setDefaultTextEncodingName("UTF-8");
        webSetting.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        webSetting.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSetting.setAllowFileAccessFromFileURLs(false);
        // 支持缩放
        webSetting.setSupportZoom(true);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
//        mWebView.addJavascriptInterface(new JavaScriptinterface(), "android");
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        String userAgent = webSetting.getUserAgentString();
        webSetting.setBlockNetworkImage(false);
        webSetting.setMixedContentMode(2);//支持http请求加载https图片资源
        mWebView.setWebChromeClient(webChromeClient);
        try {
            mWebView.loadUrl(URLDecoder.decode(mUrl, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
            } else {
                mProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                mProgressBar.setProgress(newProgress);//设置进度值
            }
        }
    };
}
