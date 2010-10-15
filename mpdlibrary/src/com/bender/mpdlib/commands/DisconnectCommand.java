package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class DisconnectCommand extends StatusCommand
{
    public DisconnectCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    public void executeCommand() throws IOException
    {
        pipe.write(MpdCommands.close.toString());
    }
}
