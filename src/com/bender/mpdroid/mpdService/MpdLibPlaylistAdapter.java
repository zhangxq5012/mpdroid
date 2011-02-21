package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.Playlist;

/**
 */
public class MpdLibPlaylistAdapter implements MpdPlaylistAdapterIF
{
    private Playlist playlist;

    public MpdLibPlaylistAdapter(Playlist playlist)
    {
        this.playlist = playlist;
    }

    public MpdSongAdapterIF getCurrentSong()
    {
        return new NullSongAdapter();
    }

    public int getPlaylistSize()
    {
        return playlist.getPlaylistLength();
    }
}
