package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.commands.Response;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public abstract class PlayerSimCommand extends SimCommand
{

    private SimPlayer simPlayer;

    public PlayerSimCommand(PrintWriter writer, SimPlayer simPlayer)
    {
        super(writer);
        this.simPlayer = simPlayer;
    }

    public void run()
    {
        try
        {
            updatePlayStatus(getPlayStatus());
            writer.println(Response.OK);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void updatePlayStatus(PlayStatus playStatus)
    {
        simPlayer.updatePlayStatus(playStatus);
    }

    protected abstract PlayStatus getPlayStatus();

}
