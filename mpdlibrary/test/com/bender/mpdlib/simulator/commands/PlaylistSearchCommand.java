package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.util.Log;

import java.util.List;

public class PlaylistSearchCommand extends SimCommand {
    @Override
    public void run(String[] commands) throws Exception {
        if (commands.length == 3) {
            String tag = commands[1];
            String query = commands[2];
            if (!(tag.equals(SongInfo.ANY) || SongInfo.SongAttributeType.parse(tag) != null)) {
                Log.d(getClass().getSimpleName(), "invalid tag: " + tag);
                printError();
                return;
            }

            List<SongInfo> result = playlist.search(tag, query);
            for (SongInfo songInfo : result) {
                for (SongInfo.SongAttributeType songAttributeType : SongInfo.SongAttributeType.values()) {
                    if (songAttributeType.equals(SongInfo.SongAttributeType.Id)) continue;
                    String value = songInfo.getValue(songAttributeType);
                    if (value != null) {
                        printWriter.println(songAttributeType + ": " + value);
                    }
                }
                SongInfo.SongAttributeType songAttributeType = SongInfo.SongAttributeType.Id;
                printWriter.println(songAttributeType + ": " + songInfo.getValue(songAttributeType));
            }
            printWriter.println(Response.OK);
        } else {
            printError();
        }
    }

    private void printError() {
        printWriter.println(Response.ACK + "[2@0] {playlistsearch} <tag> <query>");
    }
}
