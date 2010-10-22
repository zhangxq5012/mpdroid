package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.Subsystem;

import java.io.PrintWriter;

/**
 * todo: replace with documentation
 */
public class IdleSimCommand extends SimCommand
{
    private static Subsystem subsystem;
    private IdleThread idleThread;

    public IdleSimCommand(PrintWriter pipe)
    {
        super(pipe);
    }

    public void run()
    {
        idleThread = new IdleThread();
        idleThread.start();
    }

    public static void subsystemUpdated(Subsystem sub)
    {
        synchronized (IdleSimCommand.class)
        {
            subsystem = sub;
            IdleSimCommand.class.notifyAll();
        }
    }

    private class IdleThread extends Thread
    {
        @Override
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
                writer.println("changed: " + changedSubsystem);
                writer.println("OK");
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
