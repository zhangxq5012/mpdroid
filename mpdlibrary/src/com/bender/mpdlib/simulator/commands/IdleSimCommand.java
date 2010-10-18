package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.Subsystem;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public class IdleSimCommand extends SimCommand
{
    private static Subsystem subsystem;

    public IdleSimCommand(BufferedWriter pipe)
    {
        super(pipe);
    }

    public void run()
    {
        Subsystem changedSubsystem;
        try
        {
            synchronized (IdleSimCommand.class)
            {
                IdleSimCommand.class.wait();
                changedSubsystem = subsystem;
            }
            writer.write("changed: " + changedSubsystem);
            writer.write("OK");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void subsystemUpdated(Subsystem sub)
    {
        synchronized (IdleSimCommand.class)
        {
            subsystem = sub;
            IdleSimCommand.class.notifyAll();
        }
    }

}
