package com.bender.mpdlib;

import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.util.Log;

import java.util.Map;

/**
 */
public class Playlist
{
    private int playlistLength = 0;
    private static final String TAG = Playlist.class.getSimpleName();

    public int getPlaylistLength()
    {
        return playlistLength;
    }

    void processStatus(Map<MpdStatus, StatusTuple> result)
    {
        if (result.containsKey(MpdStatus.playlistlength))
        {
            playListUpdated(result.get(MpdStatus.playlistlength));
        }
    }

    private void playListUpdated(StatusTuple statusTuple)
    {
        String value = statusTuple.getValue();
        int newLength = Integer.valueOf(value);
        boolean changed;
        synchronized (this)
        {
            changed = newLength != playlistLength;
        }
        playlistLength = newLength;
        if (changed)
        {
            Log.v(TAG, "playlistLength updated: " + newLength);
        }

    }
}
