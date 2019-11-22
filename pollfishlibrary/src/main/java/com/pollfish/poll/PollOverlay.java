package com.pollfish.poll;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pollfish.poll.view.PollfishActivity;

public final class PollOverlay {
    private static final String TAG = "PollOverlay";

    /**
     * A listener that does nothing when called.
     */
    private static final PollListener nullListener = new PollListener() {};

    /**
     * A reference to a listener from the calling activity.
     * It will be called at different points of the overlay lifecycle.
     */
    @NonNull
    public static PollListener listener = nullListener;

    /**
     * A reference to the calling activity.
     */
    // Note that just a context is not enough, due to activity transitions.
    private Activity activity;

    public PollOverlay(Activity activity) {
        this.activity = activity;
    }

    /**
     * Shows the overlay, passing it the specified parameters.
     * @param listener A listener to be called at different points of the overlay lifecycle.
     */
    public void show(@Nullable final PollListener listener,
                     String param1, String param2, String param3, String param4, String param5) {
        final Intent intent = new Intent(activity, PollfishActivity.class);

        intent.putExtra(PollfishActivity.PARAM1, param1);
        intent.putExtra(PollfishActivity.PARAM2, param2);
        intent.putExtra(PollfishActivity.PARAM3, param3);
        intent.putExtra(PollfishActivity.PARAM4, param4);
        intent.putExtra(PollfishActivity.PARAM5, param5);

        PollOverlay.listener = (listener != null) ? listener : nullListener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {    // Android 5.0+
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        } else {
            activity.startActivity(intent);
        }
        Log.d(TAG, "Overlay showed");
    }
}
