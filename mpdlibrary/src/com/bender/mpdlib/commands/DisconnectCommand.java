package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

/**
 * todo: replace with documentation
 */
public class DisconnectCommand extends BasicCommand
{
    public DisconnectCommand(Pipe pipe)
    {
        super(pipe, MpdCommands.close);
    }

}
