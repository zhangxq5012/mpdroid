package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo: replace with documentation
 */
public class SimPlayer
{
    private PlayStatus currentPlayStatus = PlayStatus.Stopped;
    private final Object playStatusLock = new Object();
    private AtomicInteger currentVolume = new AtomicInteger(100);
    private SubSystemSupport subSystemSupport;

    public SimPlayer(SubSystemSupport subSystemSupport)
    {
        this.subSystemSupport = subSystemSupport;
    }

    public void updatePlayStatus(PlayStatus playStatus)
    {
        boolean changed;
        synchronized (playStatusLock)
        {
            changed = !currentPlayStatus.equals(playStatus);
            currentPlayStatus = playStatus;
        }
        if (changed)
        {
            subSystemSupport.updateSubSystemChanged(Subsystem.player);
        }
    }

    public synchronized StatusTuple getPlayStatus()
    {
        return new StatusTuple(MpdStatus.state, currentPlayStatus.serverString);
    }

    public PlayStatus getCurrentPlayStatus()
    {
        synchronized (playStatusLock)
        {
            return currentPlayStatus;
        }
    }

    public Integer getVolume()
    {
        return currentVolume.get();
    }

    public boolean setVolume(Integer volume)
    {
        if (volume == null)
        {
            throw new IllegalArgumentException("Null volume");
        }
        if (volume > 100)
        {
            volume = 100;
        }
        if (volume < 0)
        {
            volume = 0;
        }
        Integer oldVolume = currentVolume.getAndSet(volume);
        boolean changed = !oldVolume.equals(volume);
        if (changed)
        {
            subSystemSupport.updateSubSystemChanged(Subsystem.mixer);
        }
        return changed;
    }

    public StatusTuple getVolumeStatus()
    {
        return new StatusTuple(MpdStatus.volume, currentVolume.toString());
    }
}
