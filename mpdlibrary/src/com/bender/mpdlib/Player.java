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
}
