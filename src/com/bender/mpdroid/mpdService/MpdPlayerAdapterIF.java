package com.bender.mpdroid.mpdService;

/**
 * todo: replace with documentation
 */
public interface MpdPlayerAdapterIF
{
    PlayStatus getPlayStatus();

    void next();

    void prev();

    Integer setVolume(Integer volume);

    Integer getVolume();

    Boolean toggleMute();

    PlayStatus playOrPause();

    public enum PlayStatus
    {
        Playing,
        Paused,
        Stopped
    }
}
