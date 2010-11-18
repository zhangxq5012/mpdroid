package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class SeekCommand extends StatusCommand<SeekArg>
{
    public SeekCommand(Pipe commandPipe, SeekArg position)
    {
        super(commandPipe, position);
    }

    @Override
    protected void executeCommand(SeekArg seekArg) throws IOException
    {
        pipe.write(MpdCommands.seekid + " " + seekArg.toString());
    }
}
