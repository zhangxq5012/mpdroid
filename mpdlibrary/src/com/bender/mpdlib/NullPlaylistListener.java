package com.bender.mpdlib;

import com.bender.mpdlib.util.Log;

public class NullPlaylistListener implements PlaylistListener {
    public void playlistUpdated(Integer playlistLength) {
        Log.v(getClass().getSimpleName(), ".playlistUpdated(" + playlistLength + ")");
    }
}
