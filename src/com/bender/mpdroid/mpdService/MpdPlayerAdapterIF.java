package com.bender.mpdroid.mpdService;

/**
 * mpd service player interface
 */
public interface MpdPlayerAdapterIF
{
    PlayStatus getPlayStatus();

    void next();

    void prev();

    Integer setVolume(Integer volume);

    Integer getVolume();

    Boolean toggleMute();

    void playOrPause();

    void stop();

    MpdSongAdapterIF getCurrentSong();

    void addSongChangeListener(MpdSongListener listener);

    public enum PlayStatus
    {
        Playing,
        Paused,
        Stopped
    }
}
