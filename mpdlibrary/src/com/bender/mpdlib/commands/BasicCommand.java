package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class BasicCommand extends StatusCommand<NullArg>
{
    private MpdCommands command;

    public BasicCommand(Pipe pipe, MpdCommands commands)
    {
        super(pipe);
        command = commands;
    }

    @Override
    public final void executeCommand(NullArg nullArg) throws IOException
    {
        pipe.write(command.toString());
    }
}
