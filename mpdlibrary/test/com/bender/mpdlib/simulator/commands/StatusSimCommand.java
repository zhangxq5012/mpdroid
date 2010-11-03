package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.simulator.SimPlayer;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.io.IOException;
import java.io.PrintWriter;

/**
 */
public class StatusSimCommand extends SimCommand
{

    private SimPlayer simPlayer;
    private Playlist playlist;

    public StatusSimCommand(PrintWriter simBufferedWriter, SimPlayer simPlayer, Playlist playlist)
    {
        super(simBufferedWriter);
        this.simPlayer = simPlayer;
        this.playlist = playlist;
    }

    public void run() throws Exception
    {
        StatusTuple statusTuple = simPlayer.getPlayStatus();
        write(statusTuple);
        statusTuple = simPlayer.getVolumeStatus();
        write(statusTuple);
        statusTuple = playlist.getStatus();
        write(statusTuple);
        statusTuple = simPlayer.getTimeStatus();
        write(statusTuple);
        writer.println(Response.OK);
    }

    private void write(StatusTuple statusTuple) throws IOException
    {
        if (statusTuple != null)
        {
            String string = statusTuple.first() + ": " + statusTuple.second();
            Log.v(getClass().getSimpleName(), "   " + string);
            writer.println(string);
        }
    }
}
