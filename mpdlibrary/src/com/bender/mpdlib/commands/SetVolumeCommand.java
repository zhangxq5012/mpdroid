package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class SetVolumeCommand extends StatusCommand<SingleArg<Integer>>
{
    public SetVolumeCommand(Pipe pipe, Integer volume)
    {
        super(pipe, new SingleArg<Integer>(volume));
    }

    @Override
    public void executeCommand(SingleArg<Integer> volume) throws IOException
    {
        pipe.write(MpdCommands.setvol + " " + volume);
    }
}
