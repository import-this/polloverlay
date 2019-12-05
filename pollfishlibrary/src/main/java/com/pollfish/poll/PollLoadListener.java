package com.pollfish.poll;

public abstract class PollLoadListener {
    /**
     * Called when the site is loaded.
     */
    public void onPollLoaded() {}

    /**
     * Called if the site fails to load (e.g. no internet connection).
     * @param errorCode The error code corresponding to an ERROR_* value.
     */
    public void onPollFailedToLoad(int errorCode) {}
}
