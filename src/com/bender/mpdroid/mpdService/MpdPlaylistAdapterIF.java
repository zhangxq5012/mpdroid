package com.bender.mpdroid.mpdService;

/**
 * mpd service playlist interface
 */
public interface MpdPlaylistAdapterIF {
    MpdSongAdapterIF getCurrentSong();

    int getPlaylistSize();

    MpdSongAdapterIF getSongInfo(int songPosition);

    void play(int songPos);

    void setListener(MpdPlaylistListenerIF playlistListenerIF);

    interface MpdPlaylistListenerIF {
        void playlistUpdated(int newLength);
    }
}
