package com.pollfish.poll;

public abstract class PollListener {
    /**
     * Called when the overlay is opened.
     */
    public void onPollOpened() {}

    /**
     * Called when the site is loaded.
     */
    public void onPollLoaded() {}

    /**
     * Called when the overlay is closed.
     * @param param3
     * @param param4
     * @param param5
     */
    public void onPollClosed(String param3, String param4, String param5) {}

    /**
     * Called if the site fails to load (e.g. no internet connection).
     * @param errorCode
     */
    public void onPollFailedToLoad(int errorCode) {}
}
