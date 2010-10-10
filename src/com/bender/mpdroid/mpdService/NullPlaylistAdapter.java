package com.bender.mpdroid.mpdService;

/**
 * todo: replace with documentation
 */
class NullPlaylistAdapter implements MpdPlaylistAdapterIF
{
    public MpdSongAdapterIF getCurrentSong()
    {
        return new NullSongAdapter();
    }

}
