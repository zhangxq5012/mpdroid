package com.bender.mpdlib.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.Pipe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class GetStatusCommand extends Command<NullArg, Result<Map<MpdStatus, StatusTuple>>>
{
    public GetStatusCommand(Pipe pipe)
    {
        super(pipe);
    }

    @Override
    protected void executeCommand(NullArg arg) throws IOException
    {
        pipe.write(MpdCommands.status.toString());
    }

    @Override
    protected Result<Map<MpdStatus, StatusTuple>> readResult() throws IOException
    {
        Map<MpdStatus, StatusTuple> statuses = new HashMap<MpdStatus, StatusTuple>();
        String line;
        while (!isLastLine(line = pipe.readLine()))
        {
            StatusTuple statusTuple = MpdStatus.parse(line);
            statuses.put(statusTuple.getStatus(), statusTuple);
        }
        Status status = Status.parse(line);
        Result<Map<MpdStatus, StatusTuple>> listResult = new Result<Map<MpdStatus, StatusTuple>>();
        listResult.status = status;
        listResult.result = statuses;
        return listResult;
    }

    private boolean isLastLine(String line)
    {
        return Response.isResponseLine(line);
    }
}
