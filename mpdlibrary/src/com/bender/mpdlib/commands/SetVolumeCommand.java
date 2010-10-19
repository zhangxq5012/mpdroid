package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class SetVolumeCommand extends StatusCommand<Integer>
{
    public SetVolumeCommand(Pipe pipe, Integer volume)
    {
        super(pipe, volume);
    }

    @Override
    public void executeCommand(Integer volume) throws IOException
    {
        pipe.write(MpdCommands.setvol + " " + volume);
    }
}
