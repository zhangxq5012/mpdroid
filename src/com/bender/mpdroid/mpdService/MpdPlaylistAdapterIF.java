package com.bender.mpdroid.mpdService;

/**
 * mpd service playlist interface
 */
public interface MpdPlaylistAdapterIF
{
    MpdSongAdapterIF getCurrentSong();

    int getPlaylistSize();

    MpdSongAdapterIF getSongInfo(int songPosition);
}
