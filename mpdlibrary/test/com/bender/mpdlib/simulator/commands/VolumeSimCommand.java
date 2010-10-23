package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class VolumeSimCommand extends SimCommand
{
    private SimPlayer simPlayer;
    private String[] command;

    public VolumeSimCommand(PrintWriter writer, String[] command, SimPlayer simPlayer)
    {
        super(writer);
        this.simPlayer = simPlayer;
        this.command = command;
    }

    @Override
    public void run()
    {
        if (command.length == 2)
        {
            Integer volume;
            try
            {
                volume = Integer.parseInt(command[1].trim());
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
