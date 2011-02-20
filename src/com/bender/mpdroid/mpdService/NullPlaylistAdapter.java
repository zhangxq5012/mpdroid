package com.bender.mpdroid.mpdService;

/**
 * Null object playlist adapter
 */
class NullPlaylistAdapter implements MpdPlaylistAdapterIF
{
    public MpdSongAdapterIF getCurrentSong()
    {
        return new NullSongAdapter();
    }

    public int getPlaylistSize()
    {
        return 0;
    }

}
