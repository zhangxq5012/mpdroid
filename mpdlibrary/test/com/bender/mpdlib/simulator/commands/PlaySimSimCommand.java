package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.commands.Response;

/**
 */
public class PlaySimSimCommand extends PlayerSimCommand
{

    @Override
    protected PlayStatus getPlayStatus()
    {
        return PlayStatus.Playing;
    }

    @Override
    protected void runWithArgs(String[] commands)
    {
        if (commands.length != 2)
        {
            printError();
            return;
        }
        try
        {
            int playlistId = Integer.parseInt(commands[1]);
            playlist.gotoSongIndex(playlistId);
            simPlayer.next();
            run(new String[]{commands[0]});
        } catch (NumberFormatException e)
        {
            printError();
            return;
        }
    }

    private void printError()
    {
        printWriter.println(Response.ACK + "[2@0] {play} wrong number of args");
    }
}
