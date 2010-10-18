package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

/**
 * todo: replace with documentation
 */
public class PreviousCommand extends BasicCommand
{
    public PreviousCommand(Pipe commandPipe)
    {
        super(commandPipe, MpdCommands.previous);
    }
}
