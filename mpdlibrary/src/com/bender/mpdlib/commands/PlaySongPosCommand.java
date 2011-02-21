package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class PlaySongPosCommand extends StatusCommand<SingleArg<Integer>>
{
    public PlaySongPosCommand(Pipe commandPipe, int songPos)
    {
        super(commandPipe, new SingleArg<Integer>(songPos));
    }

    @Override
    protected void executeCommand(SingleArg<Integer> arg) throws IOException
    {
        pipe.write(MpdCommands.play + " " + arg);
    }
}
