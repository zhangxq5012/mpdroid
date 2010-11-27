package com.bender.mpdroid;

/**
 */
public interface MpDroidActivityWidget
{
    void onCreate(MpDroidActivity activity);

    /**
     * Called on a worker thread.
     */
    void onConnect();

    /**
     * Called on the ui thread.
     *
     * @param connected is connected?
     */
    void onConnectionChange(boolean connected);
}
