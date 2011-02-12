package com.bender.mpdlib;

/**
 */
public interface Player
{
    void play();

    PlayStatus getPlayStatus();

    void stop();

    void next();

    void pause();

    void addPlayStatusListener(PlayStatusListener listener);

    void previous();

    SongInfo getCurrentSongInfo();

    void addCurrentSongListener(CurrentSongListener currentSongListener);

    Integer getVolume();

    void setVolume(Integer volume);

    void setVolumeImpl(Integer volume);

    void addVolumeListener(VolumeListener listener);

    Boolean toggleMute();

    boolean isMuted();

    SongProgress getProgress();

    void addSongProgressListener(SongProgressListener listener);

    void seek(Integer position);
}
