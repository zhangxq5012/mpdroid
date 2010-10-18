package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * todo: replace with documentation
 */
public class ConnectCommand extends Command<Result<String>>
{
    private SocketAddress address;

    public ConnectCommand(Pipe pipe, SocketAddress theAddress)
    {
        super(pipe);
        address = theAddress;
    }

    @Override
    public void executeCommand() throws IOException
    {
        pipe.connect(address);
    }

    @Override
    public Result<String> readResult() throws IOException
    {
        String version = null;
        String line = pipe.readLine();
        Status status = Status.parse(line);
        if (status.success)
        {
            version = line.substring(Response.OK.toString().length()).trim();
        }
        Result<String> result = new Result<String>();
        result.status = status;
        result.result = version;
        return result;
    }
}
