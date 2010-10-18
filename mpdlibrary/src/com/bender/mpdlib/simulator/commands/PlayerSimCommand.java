package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public abstract class PlayerSimCommand extends SimCommand
{
    private static PlayStatus currentPlayStatus = PlayStatus.Stopped;

    public PlayerSimCommand(BufferedWriter writer)
    {
        super(writer);
    }

    public void run()
    {
        try
        {
            if (updatePlayStatus(getPlayStatus()))
            {
                IdleSimCommand.subsystemUpdated(Subsystem.player);
            }
            writer.write(Response.OK.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static synchronized boolean updatePlayStatus(PlayStatus playStatus)
    {
        boolean changed = !currentPlayStatus.equals(playStatus);
        currentPlayStatus = playStatus;
        return changed;
    }

    protected abstract PlayStatus getPlayStatus();

    public static synchronized StatusTuple getStatus()
    {
        return new StatusTuple(MpdStatus.state, currentPlayStatus.serverString);
    }

}
