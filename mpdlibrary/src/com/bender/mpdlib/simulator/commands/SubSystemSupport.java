package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: replace with documentation
 */
public class SubSystemSupport
{
    private List<Thread> waitingThreads = new ArrayList<Thread>();

    private Subsystem changedSubsystem;
    private IdleStrategy idleStrategy;

    public SubSystemSupport()
    {
        idleStrategy = new IdleStrategy()
        {
            public void execute(Runnable runnable)
            {
                Log.i("SubSystemSupport", "Using threading idle strategy");
                Thread thread = new Thread(runnable);
                thread.start();
            }
        };
    }

    public void disconnect()
    {
        synchronized (this)
        {
            for (Thread waitingThread : waitingThreads)
            {
                waitingThread.interrupt();
            }
            waitingThreads.clear();
        }
    }

    public Subsystem waitForSubSystemChange()
    {
        Subsystem ret = null;
        synchronized (this)
        {
            try
            {
                changedSubsystem = null;
                Thread thread = Thread.currentThread();
                waitingThreads.add(thread);
                wait();
                waitingThreads.remove(thread);
                ret = changedSubsystem;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public void updateSubSystemChanged(Subsystem subsystem)
    {
        synchronized (this)
        {
            changedSubsystem = subsystem;
            System.out.println(getClass().getSimpleName() + ": subsystem changed: " + changedSubsystem);
            notifyAll();
        }
    }

    public IdleStrategy getIdleStrategy()
    {
        return idleStrategy;
    }

    public void setIdleStrategy(IdleStrategy idleStrategy)
    {
        this.idleStrategy = idleStrategy;
    }

    public interface IdleStrategy
    {
        void execute(Runnable runnable);
    }
}
