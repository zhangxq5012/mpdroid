package com.bender.mpdroid.mpdService;

import java.util.List;

/**
 * mpd service playlist interface
 */
public interface MpdPlaylistAdapterIF {
    MpdSongAdapterIF getCurrentSong();

    int getPlaylistSize();

    MpdSongAdapterIF getSongInfo(int songPosition);

    void play(int songPos);

    void setListener(MpdPlaylistListenerIF playlistListenerIF);

    List<MpdSongAdapterIF> search(String query);

    void play(MpdSongAdapterIF mpdSongAdapterIF);

    interface MpdPlaylistListenerIF {
        void playlistUpdated(int newLength);
    }
}
