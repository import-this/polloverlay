package com.pollfish.poll.view.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pollfish.poll.BuildConfig;
import com.pollfish.poll.PollOverlay;
import com.pollfish.poll.R;

import java.util.Map;

/**
 * A ConstraintLayout with custom callbacks for window focus and system UI visibility changes.
 */
public class PollOverlayLayout extends ConstraintLayout {
    private static final String TAG = "PollOverlayLayout";
    /**
     * The containing activity.
     */
    private Activity activity;

    private boolean immersiveModeEnabled = true;

    public PollOverlayLayout(Context context) {
        super(context);
    }

    public PollOverlayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PollOverlayLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public static class PollOverlayListener {
        public void onPollOverlayOpened() {}
        public void onPollOverlayClosed() {}
    }

    private static final PollOverlayListener nullListener = new PollOverlayListener();

    @NonNull
    private PollOverlayListener listener = nullListener;

    public void setPollOverlayListener(@Nullable PollOverlayListener listener) {
        this.listener = (listener != null) ? listener : nullListener;
    }


    private static class AnimatorListener extends AnimatorListenerAdapter {
        private Activity activity;

        AnimatorListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            // Prevent input events while animating.
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            // Resume input events after animating.
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    private static final int ANIM_DURATION = 500;

    private float getAnimDistance() {
        return activity.getWindow().getDecorView().getWidth();
    }

    private void slideLeft(Activity activity) {
        final ObjectAnimator slideLeft = ObjectAnimator.ofFloat(this, "translationX", getAnimDistance(), 0f);
        slideLeft.setDuration(ANIM_DURATION).addListener(new AnimatorListener(activity) {
            @Override
             public void onAnimationStart(Animator animation) {
                 super.onAnimationStart(animation);
                 listener.onPollOverlayOpened();
             }
         });
        slideLeft.start();
    }
    private void slideRight(Activity activity) {
        final ObjectAnimator slideRight = ObjectAnimator.ofFloat(this, "translationX", 0f, getAnimDistance());
        slideRight.setDuration(ANIM_DURATION).addListener(new AnimatorListener(activity) {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onPollOverlayClosed();
                super.onAnimationEnd(animation);
            }
        });
        slideRight.start();
    }

    public void slideLeft() {
        slideLeft(activity);
    }
    public void slideRight() {
        slideRight(activity);
    }


    public void init(@NonNull final Activity activity, @NonNull Map<String, String> params) {
        this.activity = activity;

        ((TextView) findViewById(R.id.pollfish_param1)).setText(params.get(PollOverlay.PARAM1));
        ((TextView) findViewById(R.id.pollfish_param2)).setText(params.get(PollOverlay.PARAM2));

        final ImageButton button = findViewById(R.id.pollfish_button_close);
        // Close button callback.
        button.setOnClickListener(v -> slideRight());

        // Set in XML. Setting this to true also ensures that this view is focusable.
        // https://developer.android.com/reference/android/view/View.html#setFocusableInTouchMode(boolean)
        // https://android-developers.googleblog.com/2008/12/touch-mode.html
        //overlay.setFocusableInTouchMode(true);
        // Focus, so that it receives input events.
        requestFocusFromTouch();
        if (BuildConfig.DEBUG && !requestFocusFromTouch()) throw new AssertionError();
    }

    private static class SavedState extends BaseSavedState {
        boolean immersiveModeEnabled;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            immersiveModeEnabled = (in.readInt() == 1);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt((immersiveModeEnabled) ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();

        SavedState myState = new SavedState(superState);
        myState.immersiveModeEnabled = this.immersiveModeEnabled;

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Cast the incoming Parcelable to our custom SavedState.
        final SavedState savedState = (SavedState) state;

        super.onRestoreInstanceState(savedState.getSuperState());
        this.immersiveModeEnabled = savedState.immersiveModeEnabled;
    }


    private void hideSystemUI() {
        // Preferred approach to going fullscreen for transient states (e.g. ads).
        // https://developer.android.com/reference/android/view/View.html#SYSTEM_UI_FLAG_FULLSCREEN
        // https://developer.android.com/reference/android/view/View.html#SYSTEM_UI_FLAG_IMMERSIVE
        // https://developer.android.com/training/system-ui/immersive
        // https://developer.android.com/training/system-ui/status
        // No need to save the old system UI visibility, since the view will be removed later on.
        setSystemUiVisibility(
                ((immersiveModeEnabled) ? View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY : View.SYSTEM_UI_FLAG_IMMERSIVE)
                // Set the content to appear under the system bars (status bar, nav bar) so
                // that the content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                // and status bar.
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    /**
     *
     * @param immersiveModeEnabled Controls whether the overlay is shown in sticky immersive mode.
     *      If true, SYSTEM_UI_FLAG_IMMERSIVE_STICKY will be set.
     */
    public void hideSystemUI(boolean immersiveModeEnabled) {
        Log.d(TAG, "Hiding system UI with IMMERSIVE" +
                ((immersiveModeEnabled) ? "_STICKY" : "") + " mode enabled");
        this.immersiveModeEnabled = immersiveModeEnabled;
        hideSystemUI();
    }

    /*public void showSystemUI() {
        //setSystemUiVisibility(oldSystemUiVisibility);
    }*/

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d(TAG, "Window focus changed to " + ((hasWindowFocus) ? "focused" : "unfocused"));
        super.onWindowFocusChanged(hasWindowFocus);
        // Re-hide the system bars when gaining window focus.
        if (hasWindowFocus) {
            hideSystemUI();
        }
    }

    /*@Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        switch (visibility) {
            case VISIBLE:
                break;
            case INVISIBLE:
                break;
            case GONE:
                break;
        }
    }*/


    // Hardware key events run from the top of the view tree down to the currently focused view.
    // dispatchKeyEvent(PreIme) are the only methods that are always called, since they execute
    // before onKey*/setOnKeyListener methods. Overload to prevent key events from being called.
    // https://developer.android.com/guide/topics/ui/ui-events
    // https://stackoverflow.com/a/12230251/1751037

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // If the key event was the Back button being released
        //if (event.getKeyCode() == KeyEvent.KEYCODE_BACK/* && event.getAction() == KeyEvent.ACTION_UP*/) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            final WebView pollWebView = findViewById(R.id.pollfish_webview);

            if (pollWebView.canGoBack()) {
                pollWebView.goBack();
            } else {
                slideRight(activity);   // Close the overlay.
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /*@Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return super.onKeyPreIme(keyCode, event);
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*// If the key event was the Back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // and if there's history
            if (pollWebView.canGoBack()) {
                pollWebView.goBack();
            } else {
                // Close the overlay.

            }
            return true;
        }*/
        // Bubble up to the default system behavior for other events.
        return super.onKeyDown(keyCode, event);
    }
}
