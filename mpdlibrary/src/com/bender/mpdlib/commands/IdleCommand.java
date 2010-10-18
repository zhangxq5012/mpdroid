package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.Subsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * todo: replace with documentation
 */
public class IdleCommand extends Command<Result<List<Subsystem>>>
{
    private Subsystem[] subsystems;

    public IdleCommand(Pipe pipe, Subsystem[] subsystems)
    {
        super(pipe);
        this.subsystems = subsystems;
    }

    @Override
    public void executeCommand() throws IOException
    {
        StringBuffer arguments = new StringBuffer("");
        for (Subsystem subsystem : subsystems)
        {
            arguments.append(" ").append(subsystem.toString());
        }
        pipe.write(MpdCommands.idle.toString() + arguments.toString());
    }

    @Override
    public Result<List<Subsystem>> readResult() throws IOException
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
