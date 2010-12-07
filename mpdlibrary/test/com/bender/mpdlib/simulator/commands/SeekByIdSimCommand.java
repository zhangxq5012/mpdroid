package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.util.Log;

/**
 * Arguments are (songid, position)
 */
public class SeekByIdSimCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        if (commands.length == 3)
        {
            try
            {
                Integer songId = Integer.valueOf(commands[1]);
                Integer position = Integer.valueOf(commands[2]);
                Log.v(TAG, " songid=" + songId);
                Log.v(TAG, " position=" + position);
                simPlayer.seek(songId, position);
                printWriter.println(Response.OK);
            } catch (NumberFormatException e)
            {
                printWriter.println(Response.ACK + " [2@0] {seekid} needs an integer");
                Log.e(TAG, e);
            }
        } else
        {
            printWriter.println(Response.ACK + " [2@0] {seekid} wrong number of arguments for \"seekid\"");
            Log.w(TAG, "seekid: wrong number of arguments " + commands.length);
        }
    }
}
