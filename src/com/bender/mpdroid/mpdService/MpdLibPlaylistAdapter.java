package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.Playlist;
import com.bender.mpdlib.PlaylistListener;
import com.bender.mpdlib.SongInfo;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class MpdLibPlaylistAdapter implements MpdPlaylistAdapterIF {
    private Playlist playlist;

    public MpdLibPlaylistAdapter(Playlist playlist) {
        this.playlist = playlist;
    }

    public MpdSongAdapterIF getCurrentSong() {
        return new NullSongAdapter();
    }

    public int getPlaylistSize() {
        return playlist.getPlaylistLength();
    }

    public MpdSongAdapterIF getSongInfo(int songPosition) {
        return new MpdLibSongAdapter(playlist.getPlaylistInfo(songPosition));
    }

    public void play(int songPos) {
        playlist.play(songPos);
    }

    public void setListener(final MpdPlaylistListenerIF playlistListenerIF) {
        playlist.setListener(new PlaylistListener() {
            public void playlistUpdated(Integer playlistLength) {
                playlistListenerIF.playlistUpdated(playlistLength);
            }
        });
    }

    public List<MpdSongAdapterIF> search(String query) {
        final List<SongInfo> songInfoList = playlist.searchAll(query);
        List<MpdSongAdapterIF> result = new ArrayList<MpdSongAdapterIF>(songInfoList.size());
        for (SongInfo songInfo : songInfoList) {
            result.add(new MpdLibSongAdapter(songInfo));
        }
        return result;
    }

    public void play(MpdSongAdapterIF mpdSongAdapterIF) {
        int id = mpdSongAdapterIF.getId();
        playlist.playid(id);
    }
}
