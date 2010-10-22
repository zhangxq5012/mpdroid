package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class StatusSimCommand extends SimCommand
{

    public StatusSimCommand(PrintWriter simBufferedWriter)
    {
        super(simBufferedWriter);
    }

    public void run() throws Exception
    {
        StatusTuple statusTuple = PlayerSimCommand.getStatus();
        write(statusTuple);
        statusTuple = VolumeSimCommand.getStatus();
        write(statusTuple);
        statusTuple = Playlist.getStatus();
        write(statusTuple);
        writer.println(Response.OK);
    }

    private void write(StatusTuple statusTuple) throws IOException
    {
        String string = statusTuple.first() + ": " + statusTuple.second();
        System.out.println(getClass().getSimpleName() + "   " + string);
        writer.println(string);
    }
}
