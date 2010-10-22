package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class VolumeSimCommand extends SimCommand
{
    private StringTokenizer stringTokenizer;
    private SimPlayer simPlayer;

    public VolumeSimCommand(PrintWriter writer, StringTokenizer tokenizer, SimPlayer simPlayer)
    {
        super(writer);
        this.simPlayer = simPlayer;
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
                setVolume(volume);
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

    private void setVolume(Integer volume)
    {
        simPlayer.setVolume(volume);
    }
}
