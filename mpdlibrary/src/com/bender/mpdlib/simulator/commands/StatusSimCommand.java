package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class StatusSimCommand extends SimCommand
{

    public StatusSimCommand(BufferedWriter simBufferedWriter)
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
        writer.write(Response.OK.toString());
    }

    private void write(StatusTuple statusTuple) throws IOException
    {
        writer.write(statusTuple.first() + ": " + statusTuple.second());
    }
}
