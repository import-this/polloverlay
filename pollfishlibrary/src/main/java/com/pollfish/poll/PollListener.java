package com.pollfish.poll;

import androidx.annotation.NonNull;

import java.util.Map;

public abstract class PollListener {
    /**
     * Called when the overlay is opened.
     */
    public void onPollOpened() {}

    /**
     * Called when the overlay is closed.
     * @param returnVals A map of return values returned from the overlay.
     */
    public void onPollClosed(@NonNull Map<String, String> returnVals) {}
}
