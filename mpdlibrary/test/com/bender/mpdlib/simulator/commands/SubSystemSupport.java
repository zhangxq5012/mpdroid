package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class SubSystemSupport
{
    private List<Thread> waitingThreads = new ArrayList<Thread>();

    private Subsystem changedSubsystem;
    private IdleStrategy idleStrategy;

    public SubSystemSupport()
    {
        setIdleStrategy(new ThreadingIdleStrategy());
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
            Log.v(getClass().getSimpleName(), ": subsystem changed: " + changedSubsystem);
            notifyAll();
        }
    }

    public IdleStrategy getIdleStrategy()
    {
        return idleStrategy;
    }

    public void setIdleStrategy(IdleStrategy idleStrategy)
    {
        Log.i("SubSystemSupport", "Using " + idleStrategy.getClass().getSimpleName());
        this.idleStrategy = idleStrategy;
    }

    public interface IdleStrategy
    {
        void execute(Runnable runnable);
    }

    private static class ThreadingIdleStrategy implements IdleStrategy
    {
        public void execute(Runnable runnable)
        {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
