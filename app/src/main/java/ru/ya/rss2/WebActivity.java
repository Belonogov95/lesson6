package ru.ya.rss2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by vanya on 17.12.14.
 */
public class WebActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("start", "web activity");
        setContentView(R.layout.web_activity);
        Intent intent = getIntent();
        String url = intent.getStringExtra(RSSSQLiteHelper.COLUMN_URL);
        Log.e("url: ", url);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });
        webView.loadUrl(url);
    }
}
