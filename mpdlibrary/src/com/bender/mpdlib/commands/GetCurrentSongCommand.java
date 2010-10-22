package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.SongInfo;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class GetCurrentSongCommand extends Command<NullArg, Result<SongInfo>>
{

    public GetCurrentSongCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    protected void executeCommand(NullArg arg) throws IOException
    {
        pipe.write(MpdCommands.currentsong);
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

