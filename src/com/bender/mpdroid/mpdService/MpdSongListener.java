package com.bender.mpdroid.mpdService;

/**
 * Listener for when song information is updated.
 */
public interface MpdSongListener
{
    void songUpdated(MpdSongAdapterIF song);
}
