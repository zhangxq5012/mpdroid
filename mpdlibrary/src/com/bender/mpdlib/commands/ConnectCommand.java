package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;
import java.net.SocketAddress;

/**
 */
public class ConnectCommand extends Command<SingleArg<SocketAddress>, Result<String>>
{
    public ConnectCommand(Pipe pipe, SocketAddress theAddress)
    {
        super(pipe, new SingleArg<SocketAddress>(theAddress));
    }

    @Override
    public void executeCommand(SingleArg<SocketAddress> address) throws IOException
    {
        pipe.connect(address.getArg());
    }

    @Override
    public Result<String> readResult() throws IOException
    {
        String version = null;
        String line = pipe.readLine();
        Status status = Status.parse(line);
        if (status.isSuccessful())
        {
            version = line.substring(Response.OK.toString().length()).trim();
        }
        Result<String> result = new Result<String>();
        result.status = status;
        result.result = version;
        return result;
    }
}
