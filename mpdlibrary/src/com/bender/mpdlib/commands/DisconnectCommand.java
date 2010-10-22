package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class DisconnectCommand extends Command<NullArg, NullArg>
{
    public DisconnectCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    protected void executeCommand(NullArg arg) throws IOException
    {
        pipe.write(MpdCommands.close);
    }

    @Override
    protected NullArg readResult() throws IOException
    {
        return null;
    }

}
