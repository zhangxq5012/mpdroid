package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;

import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class VolumeSimCommand extends SimCommand
{
    private static Integer currentVolume = 100;
    private StringTokenizer stringTokenizer;

    public VolumeSimCommand(PrintWriter writer, StringTokenizer tokenizer)
    {
        super(writer);
        stringTokenizer = tokenizer;
    }

    @Override
    public void run()
    {
        if (stringTokenizer.hasMoreTokens())
        {
            Integer volume;
            try
            {
                volume = Integer.parseInt(stringTokenizer.nextToken());
                if (setVolume(volume))
                {
                    System.out.println(getClass().getSimpleName() + ": volume= " + currentVolume);
                    IdleSimCommand.subsystemUpdated(Subsystem.mixer);
                }
                writer.println(Response.OK);
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                writer.println(Response.ACK + " [2@0] {setvol} need an integer");
            }
        }
        else
        {
            writer.println(Response.ACK + " [2@0] {setvol} wrong number of arguments for \"volume\"");
        }
    }

    private static synchronized boolean setVolume(Integer volume)
    {
        boolean changed = currentVolume != volume;
        currentVolume = volume;
        return changed;
    }

    public static synchronized StatusTuple getStatus()
    {
        return new StatusTuple(MpdStatus.volume, currentVolume.toString());
    }
}
