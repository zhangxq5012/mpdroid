package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public abstract class StatusCommand extends Command<Status>
{
    public StatusCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    public final Status readResult() throws IOException
    {
        String line = pipe.readLine();
        Status status = Status.parse(line);
        return status;
    }
}
