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

    void addPlayStatusListener(MpdPlayStatusListener listener);

    void addVolumeListener(MpdVolumeListener listener);

    public enum PlayStatus
    {
        Playing,
        Paused,
        Stopped
    }

    /**
     * todo: replace with documentation
     */
    interface MpdPlayStatusListener
    {
        void playStatusUpdated(PlayStatus playStatus);
    }

    interface MpdVolumeListener
    {
        void volumeUpdated(Integer volume);
    }
}
