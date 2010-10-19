package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class VolumeSimCommand extends SimCommand
{
    private static Integer currentVolume = 100;
    private StringTokenizer stringTokenizer;

    public VolumeSimCommand(BufferedWriter writer, StringTokenizer tokenizer)
    {
        super(writer);
        stringTokenizer = tokenizer;
    }

    @Override
    public void run()
    {
        try
        {
            if (stringTokenizer.hasMoreTokens())
            {
                Integer volume;
                try
                {
                    volume = Integer.parseInt(stringTokenizer.nextToken());
                    if (setVolume(volume))
                    {
                        IdleSimCommand.subsystemUpdated(Subsystem.mixer);
                    }
                    writer.write(Response.OK.toString());
                }
                catch (NumberFormatException e)
                {
                    e.printStackTrace();
                    writer.write(Response.ACK.toString() + " [2@0] {setvol} need an integer");
                }
            }
            else
            {
                writer.write(Response.ACK.toString() + " [2@0] {setvol} wrong number of arguments for \"volume\"");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
