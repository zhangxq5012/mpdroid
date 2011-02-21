package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.SongInfo;

import java.io.IOException;

/**
 */
public class GetPlaylistInfoCommand extends Command<SingleArg<Integer>, Result<SongInfo>>
{
    public GetPlaylistInfoCommand(Pipe commandPipe, int pos)
    {
        super(commandPipe, new SingleArg<Integer>(pos));
    }

    @Override
    protected void executeCommand(SingleArg<Integer> arg) throws IOException
    {
        pipe.write(MpdCommands.playlistinfo + " " + arg);
    }

    @Override
    protected Result<SongInfo> readResult() throws IOException
    {
        SongInfo songInfo = new SongInfo();

        String line;
        while (!Response.isResponseLine(line = pipe.readLine()))
        {
            songInfo.updateValue(line);
        }
        Status status = Status.parse(line);
        Result<SongInfo> result = new Result<SongInfo>();
        result.status = status;
        result.result = songInfo;
        return result;
    }
}
