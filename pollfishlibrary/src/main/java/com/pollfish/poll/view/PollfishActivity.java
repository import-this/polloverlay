package com.pollfish.poll.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pollfish.poll.BuildConfig;
import com.pollfish.poll.PollOverlay;
import com.pollfish.poll.R;

public class PollfishActivity extends AppCompatActivity {
    private static final String TAG = "PollfishActivity";

    public static final String PARAM1 = "com.pollfish.poll.PARAM1";
    public static final String PARAM2 = "com.pollfish.poll.PARAM2";
    public static final String PARAM3 = "com.pollfish.poll.PARAM3";
    public static final String PARAM4 = "com.pollfish.poll.PARAM4";
    public static final String PARAM5 = "com.pollfish.poll.PARAM5";

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if ("www.pollfish.com".equals(Uri.parse(url).getHost())) {
                // This is our website, so do not override! Let my WebView load the page.
                return false;
            }
            // The link is not for a page on our site, so launch another Activity that handles URLs.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            switch (errorCode) {

            }
        }
    }

    private WebView pollWebView;

    private String param3;
    private String param4;
    private String param5;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // Android 5.0+
            final Window window = getWindow();
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            window.setAllowEnterTransitionOverlap(true);
            window.setAllowReturnTransitionOverlap(true);
        }*/

        // Fullscreen
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_pollfish);

        // Activity transitions
        // We handle activity transitions in XML.
        // Note that handling transitions here after layout inflation has a different result.
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // Android 5.0+
            final Window window = getWindow();
            window.setEnterTransition(new Slide(Gravity.RIGHT));
            window.setExitTransition(new Slide(Gravity.LEFT));
        }*/

        final Intent intent = getIntent();
        if (BuildConfig.DEBUG &&
                intent.getStringExtra(PARAM1) == null ||
                intent.getStringExtra(PARAM2) == null ||
                intent.getStringExtra(PARAM3) == null ||
                intent.getStringExtra(PARAM4) == null ||
                intent.getStringExtra(PARAM5) == null) {
            throw new AssertionError();
        }
        /*
         * "When you open an activity from an intent, the bundle of extras is delivered to the
         * activity both when the configuration changes and when the system restores the activity."
         * https://developer.android.com/topic/libraries/architecture/saving-states.html
         */
        if (savedInstanceState == null) {
            ((TextView) findViewById(R.id.param1)).setText(intent.getStringExtra(PARAM1));
            ((TextView) findViewById(R.id.param2)).setText(intent.getStringExtra(PARAM2));
            param3 = intent.getStringExtra(PARAM3);
            param4 = intent.getStringExtra(PARAM4);
            param5 = intent.getStringExtra(PARAM5);
        } else {
            param3 = intent.getStringExtra(PARAM3);
            param4 = intent.getStringExtra(PARAM4);
            param5 = intent.getStringExtra(PARAM5);
        }

        pollWebView = findViewById(R.id.webview);
        final WebSettings webSettings = pollWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        pollWebView.setWebViewClient(new MyWebViewClient());
        pollWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                final String msg = String.format("%s: %d: %s", cm.sourceId(), cm.lineNumber(), cm.message());
                switch (cm.messageLevel()) {
                    case TIP:
                        Log.v(TAG, msg); break;
                    case LOG:
                        Log.i(TAG, msg); break;
                    case WARNING:
                        Log.w(TAG, msg); break;
                    case ERROR:
                        Log.e(TAG, msg); break;
                    case DEBUG:
                        Log.d(TAG, msg); break;
                }
                return true;
            }
        });

        pollWebView.loadUrl("https://www.pollfish.com/");

        PollOverlay.listener.onPollOpened();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            // Call poll listener, if any.
            PollOverlay.listener.onPollClosed(param3, param4, param5);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // If the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pollWebView.canGoBack()) {
                pollWebView.goBack();
                return true;
            }
        }
        // Bubble up to the default system behavior (probably exit the activity).
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Close button callback.
     */
    public void close(View view) {
        // Activity transitions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // Android 5.0+
            this.finishAfterTransition();
        } else {
            this.finish();
        }
    }
}
