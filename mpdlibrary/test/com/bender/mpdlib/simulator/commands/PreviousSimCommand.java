package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.library.Playlist;

import java.io.PrintWriter;

/**
 */
public class PreviousSimCommand extends SimCommand
{
    private Playlist playlist;

    public PreviousSimCommand(PrintWriter simBufferedWriter, Playlist playlist)
    {
        super(simBufferedWriter);
        this.playlist = playlist;
    }

    @Override
    public void run() throws Exception
    {
        writer.println(Response.OK);
        playlist.previous();
    }
}
