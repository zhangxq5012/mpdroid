package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class PlayCommand extends StatusCommand
{
    public PlayCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    public void executeCommand() throws IOException
    {
        pipe.write(MpdCommands.play.toString());
    }

}
