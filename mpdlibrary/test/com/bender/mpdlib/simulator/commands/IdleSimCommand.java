package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.simulator.SubSystemSupport;

import java.io.PrintWriter;

/**
 */
public class IdleSimCommand extends SimCommand
{
    private IdleRunnable idleRunnable;

    public IdleSimCommand()
    {
        idleRunnable = new IdleRunnable();
    }

    public void run(String[] commands)
    {
        idleRunnable.setContext(printWriter, subSystemSupport);
        subSystemSupport.getIdleStrategy().execute(idleRunnable);
    }

    private class IdleRunnable implements Runnable
    {
        private PrintWriter printWriter;
        private SubSystemSupport subSystemSupport;

        public void run()
        {
            Subsystem changedSubsystem = subSystemSupport.waitForSubSystemChange();
            printWriter.println("changed: " + changedSubsystem);
            printWriter.println("OK");
        }

        public void setContext(PrintWriter printWriter, SubSystemSupport subSystemSupport)
        {
            this.printWriter = printWriter;
            this.subSystemSupport = subSystemSupport;
        }
    }
}
