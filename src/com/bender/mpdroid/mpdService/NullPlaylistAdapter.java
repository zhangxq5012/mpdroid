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

    public MpdSongAdapterIF getSongInfo(int songPosition)
    {
        return new NullSongAdapter();
    }

    public void play(int songPos)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
