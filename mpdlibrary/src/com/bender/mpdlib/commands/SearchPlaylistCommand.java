package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.SongInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class SearchPlaylistCommand extends Command<SingleArg<String>, Result<List<SongInfo>>> {
    public SearchPlaylistCommand(Pipe commandPipe, String query) {
        super(commandPipe, new SingleArg<String>(query));
    }

    @Override
    protected void executeCommand(SingleArg<String> arg) throws IOException {
        pipe.write(MpdCommands.playlistsearch + " any \"" + arg + "\"");
    }

    @Override
    protected Result<List<SongInfo>> readResult() throws IOException {
        List<SongInfo> queryResult = new ArrayList<SongInfo>();
        SongInfo songInfo = new SongInfo();
        String line;
        while (!Response.isResponseLine(line = pipe.readLine())) {
            songInfo.updateValue(line);
            if (songInfo.getValue(SongInfo.SongAttributeType.Id) != null) {
                queryResult.add(songInfo);
                songInfo = new SongInfo();
            }
        }
        Status status = Status.parse(line);
        Result<List<SongInfo>> result = new Result<List<SongInfo>>();
        result.status = status;
        result.result = queryResult;
        return result;
    }
}
