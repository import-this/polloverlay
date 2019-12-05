package com.pollfish.poll;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.pollfish.poll.view.custom.PollOverlayLayout;
import com.pollfish.poll.web.PollWebChromeClient;
import com.pollfish.poll.web.PollWebViewClient;

import java.util.Map;

import static com.pollfish.poll.web.PollWebViewClient.POLLFISH_URL;

public final class PollOverlay {
    private static final String TAG = "PollOverlay";

    public static final String PARAM1 = "com.pollfish.poll.PARAM1";
    public static final String PARAM2 = "com.pollfish.poll.PARAM2";
    public static final String PARAM3 = "com.pollfish.poll.PARAM3";
    public static final String PARAM4 = "com.pollfish.poll.PARAM4";
    public static final String PARAM5 = "com.pollfish.poll.PARAM5";

    /**
     * A PollLoadListener that does nothing when called.
     */
    @SuppressWarnings("WeakerAccess")
    public static final PollLoadListener nullLoadListener = new PollLoadListener() {};
    /**
     * A pollListener that does nothing when called.
     */
    @SuppressWarnings("WeakerAccess")
    public static final PollListener nullListener = new PollListener() {};

    /**
     * A reference to the calling context.
     */
    @NonNull
    private final Context context;
    /**
     * A reference to a PollLoadListener from the calling context.
     * It will be called at different points of the overlay loading lifecycle.
     */
    @NonNull
    private PollLoadListener pollLoadListener = nullLoadListener;
    /**
     * A reference to a PollListener from the calling context.
     * It will be called at different points of the overlay lifecycle.
     */
    @NonNull
    private PollListener pollListener = nullListener;
    /**
     * A map of return values returned from the overlay.
     */
    private Map<String, String> pollReturnVals = new ArrayMap<>(3);

    private PollOverlayLayout overlay;

    public PollOverlay(@NonNull final Context context) {
        this.context = context;
    }

    /**
     * Request the decor view to go fullscreen.
     */
    private void fullscreen(boolean immersiveModeEnabled) {
        //final View decorView = activity.getWindow().getDecorView();
        // No need to save the old system UI visibility, since we set it on the overlay view itself.
        // Removing the view later will restore the original window system UI visibility.
        //oldSystemUiVisibility = decorView.getSystemUiVisibility();
        //decorView.setSystemUiVisibility(
        overlay.hideSystemUI(immersiveModeEnabled);
    }

    private void addOverlay(@NonNull final Activity activity, @NonNull Map<String, String> params) {
        overlay = (PollOverlayLayout) activity.getLayoutInflater().inflate(R.layout.overlay_pollfish, null);
        //overlay = (PollOverlayLayout) activity.getLayoutInflater().inflate(R.layout.overlay_pollfish,
        //        (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content));
        overlay.init(activity, params);

        // Passing null as LayoutParams will throw.
        activity.addContentView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        overlay.setPollOverlayListener(new PollOverlayLayout.PollOverlayListener() {
            @Override
            public void onPollOverlayOpened() {

            }
            @Override
            public void onPollOverlayClosed() {
                // Destroy the overlay after it is hidden.
                ((ViewGroup) overlay.getParent()).removeView(overlay);
                pollListener.onPollClosed(pollReturnVals);
            }
        });
        overlay.slideLeft();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setPollWebView(@NonNull final PollLoadListener listener) {
        final WebView pollWebView = overlay.findViewById(R.id.pollfish_webview);

        final WebSettings webSettings = pollWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        pollWebView.setWebViewClient(new PollWebViewClient(context, listener));
        pollWebView.setWebChromeClient(new PollWebChromeClient());

        pollWebView.loadUrl(POLLFISH_URL);
    }

    /**
     * Shows the overlay, passing it the specified parameters.
     * @param loadListener A listener to be called at different points of the overlay loading lifecycle.
     * @param listener A listener to be called at different points of the overlay lifecycle.
     * @param params A map of parameters to pass to the overlay.
     * @param immersiveModeEnabled Controls whether the overlay is shown in sticky immersive mode.
     *      If true, SYSTEM_UI_FLAG_IMMERSIVE_STICKY will be set when overlay is displayed.
     */
    @MainThread
    @SuppressWarnings("WeakerAccess")
    public void show(@NonNull Activity activity, @NonNull PollLoadListener loadListener,
                     @NonNull PollListener listener, @NonNull Map<String, String> params,
                     boolean immersiveModeEnabled) {
        if (BuildConfig.DEBUG &&
                !params.containsKey(PollOverlay.PARAM1) ||
                !params.containsKey(PollOverlay.PARAM2) ||
                !params.containsKey(PollOverlay.PARAM3) ||
                !params.containsKey(PollOverlay.PARAM4) ||
                !params.containsKey(PollOverlay.PARAM5)) {
            throw new AssertionError();
        }

        pollLoadListener = loadListener;
        pollListener = listener;
        pollReturnVals.put(PollOverlay.PARAM3, params.get(PARAM3));
        pollReturnVals.put(PollOverlay.PARAM4, params.get(PARAM4));
        pollReturnVals.put(PollOverlay.PARAM5, params.get(PARAM5));

        addOverlay(activity, params);
        fullscreen(immersiveModeEnabled);
        setPollWebView(pollLoadListener);

        pollListener.onPollOpened();
        Log.d(TAG, "Overlay showed");
    }

    /**
     * Shows the overlay in sticky immersive mode, passing it the specified parameters.
     * @param loadListener A listener to be called at different points of the overlay loading lifecycle.
     * @param listener A listener to be called at different points of the overlay lifecycle.
     * @param params A map of parameters to pass to the overlay.
     */
    @MainThread
    public void show(@NonNull Activity activity, @NonNull PollLoadListener loadListener,
                     @NonNull PollListener listener, @NonNull Map<String, String> params) {
        show(activity, loadListener, listener, params, true);
    }
}
