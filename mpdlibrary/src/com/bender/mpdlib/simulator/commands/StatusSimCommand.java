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
        writer.println(Response.OK);
    }

    private void write(StatusTuple statusTuple) throws IOException
    {
        String string = statusTuple.first() + ": " + statusTuple.second();
        System.out.println(getClass().getSimpleName() + "   " + string);
        writer.println(string);
    }
}
