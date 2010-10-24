package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.Subsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class IdleCommand extends Command<Subsystem[], Result<List<Subsystem>>>
{
    public IdleCommand(Pipe pipe)
    {
        this(pipe, new Subsystem[]{});
    }

    public IdleCommand(Pipe pipe, Subsystem[] subsystems)
    {
        super(pipe, subsystems);
    }

    @Override
    protected void executeCommand(Subsystem[] arg) throws IOException
    {
        StringBuffer arguments = new StringBuffer("");
        for (Subsystem subsystem : arg)
        {
            arguments.append(" ").append(subsystem.toString());
        }
        pipe.write(MpdCommands.idle.toString() + arguments.toString());
    }

    @Override
    protected Result<List<Subsystem>> readResult() throws IOException
    {
        String line;
        List<Subsystem> ret = new ArrayList<Subsystem>();
        while (!Response.isResponseLine(line = pipe.readLine()))
        {
            Subsystem subsystem = Subsystem.parse(line);
            ret.add(subsystem);
        }
        Status status = Status.parse(line);
        Result<List<Subsystem>> result = new Result<List<Subsystem>>();
        result.status = status;
        result.result = ret;
        return result;
    }
}
