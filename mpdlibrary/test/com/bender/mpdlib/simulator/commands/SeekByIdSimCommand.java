package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.SimPlayer;

import java.io.PrintWriter;

/**
 * Arguments are (songid, position)
 */
public class SeekByIdSimCommand extends SimCommand
{
    private SimPlayer simPlayer;
    private String[] strings;

    /**
     * Arguments are (songid, position)
     */
    public SeekByIdSimCommand(PrintWriter printWriter, String[] strings, SimPlayer simPlayer)
    {
        super(printWriter);
        this.simPlayer = simPlayer;
        this.strings = strings;
    }

    @Override
    public void run() throws Exception
    {
        if (strings.length == 3)
        {
            try
            {
                Integer songId = Integer.valueOf(strings[1]);
                Integer position = Integer.valueOf(strings[2]);
                simPlayer.seek(songId, position);
                writer.println(Response.OK);
            }
            catch (NumberFormatException e)
            {
                writer.println(Response.ACK + " [2@0] {seekid} needs an integer");
            }
        }
        else
        {
            writer.println(Response.ACK + " [2@0] {seekid} wrong number of arguments for \"seekid\"");
        }
    }
}
