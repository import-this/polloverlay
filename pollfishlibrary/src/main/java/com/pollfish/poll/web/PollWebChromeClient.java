package com.pollfish.poll.web;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.pollfish.poll.BuildConfig;

import java.util.Locale;

public class PollWebChromeClient extends WebChromeClient {
    private static final String TAG = "WebChromeClient";

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        final String msg = String.format(Locale.US,
                "%s: %d: %s", cm.sourceId(), cm.lineNumber(), cm.message());
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

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Loading progress " + newProgress);
            if (newProgress == 100) {
                Log.d(TAG, "Loaded");
            }
        }
    }
}
