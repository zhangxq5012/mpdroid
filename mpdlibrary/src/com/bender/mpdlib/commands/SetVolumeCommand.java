package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class SetVolumeCommand extends StatusCommand
{
    private Integer volume;

    public SetVolumeCommand(Pipe pipe, Integer volume)
    {
        super(pipe);
        this.volume = volume;
    }

    @Override
    public void executeCommand() throws IOException
    {
        pipe.write(MpdCommands.setvol + " " + volume);
    }
}
