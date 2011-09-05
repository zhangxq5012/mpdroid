package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;
import com.bender.mpdlib.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Playlist {
    private int playlistLength = 0;
    private static final String TAG = Playlist.class.getSimpleName();
    private Pipe commandPipe;
    private PlaylistListener listener = new NullPlaylistListener();

    public Playlist(Pipe commandPipe) {
        this.commandPipe = commandPipe;
    }

    public int getPlaylistLength() {
        return playlistLength;
    }

    void processStatus(Map<MpdStatus, StatusTuple> result) {
        if (result.containsKey(MpdStatus.playlistlength)) {
            playListUpdated(result.get(MpdStatus.playlistlength));
        }
    }

    private void playListUpdated(StatusTuple statusTuple) {
        String value = statusTuple.getValue();
        int newLength = Integer.valueOf(value);
        boolean changed;
        synchronized (this) {
            changed = newLength != playlistLength;
        }
        playlistLength = newLength;
        if (changed) {
            Log.v(TAG, "playlistLength updated: " + newLength);
            listener.playlistUpdated(newLength);
        }

    }

    public SongInfo getPlaylistInfo(int songPos) {
        Result<SongInfo> songInfoResult = CommandRunner.runCommand(new GetPlaylistInfoCommand(commandPipe, songPos));
        return songInfoResult == null ? new SongInfo() : songInfoResult.result;
    }

    public void play(int songPos) {
        CommandRunner.runCommand(new PlaySongPosCommand(commandPipe, songPos));
    }

    public void setListener(PlaylistListener listener) {
        this.listener = listener;
    }

    public List<SongInfo> searchAll(String query) {
        Result<List<SongInfo>> searchResult = CommandRunner.runCommand(new SearchPlaylistCommand(commandPipe, query));
        return searchResult == null ? new ArrayList<SongInfo>(0) : searchResult.result;
    }
}
