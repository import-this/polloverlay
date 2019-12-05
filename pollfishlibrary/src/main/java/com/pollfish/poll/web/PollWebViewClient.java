package com.pollfish.poll.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import com.pollfish.poll.PollLoadListener;

public final class PollWebViewClient extends WebViewClient {
    private static final String TAG = "PollWebViewClient";

    private static final String POLLFISH_HOST = "www.pollfish.com";
    public static final String POLLFISH_URL = "https://" + POLLFISH_HOST;

    private final Context context;
    private final PollLoadListener listener;

    public PollWebViewClient(@NonNull Context context, @NonNull final PollLoadListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (POLLFISH_HOST.equals(Uri.parse(url).getHost())) {
            // This is our website, so do not override! Let our WebView load the page.
            return false;
        }
        // The link is not for a page on our site, so launch another Activity that handles URLs.
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.d(TAG, "Loading " + url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        listener.onPollLoaded();
        Log.d(TAG, "Loaded " + url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        switch (errorCode) {

        }
        listener.onPollFailedToLoad(errorCode);
    }
}
