package com.bender.mpdroid.mpdService;

import org.bff.javampd.MPDPlaylist;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlaylistException;

/**
 * JavaMPD implementation of #MpdPlaylistAdapterIF
 */
class JavaMPDPlaylistAdapter implements MpdPlaylistAdapterIF
{
    private MPDPlaylist mpdPlaylist;

    public JavaMPDPlaylistAdapter(MPDPlaylist mpdPlaylist)
    {
        this.mpdPlaylist = mpdPlaylist;
    }

    public MpdSongAdapterIF getCurrentSong()
    {
        MpdSongAdapterIF song = new NullSongAdapter();
        try
        {
            return new JavaMPDSongAdapter(mpdPlaylist.getCurrentSong());
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
        }
        catch (MPDPlaylistException e)
        {
            e.printStackTrace();
        }
        return song;
    }
}
