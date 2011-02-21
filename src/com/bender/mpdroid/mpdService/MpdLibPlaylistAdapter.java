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

    public MpdSongAdapterIF getSongInfo(int songPosition)
    {
        return new MpdLibSongAdapter(playlist.getPlaylistInfo(songPosition));
    }

    public void play(int songPos)
    {
        playlist.play(songPos);
    }

}
