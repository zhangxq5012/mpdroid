package com.bender.mpdlib.simulator;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.math.BigInteger;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class SimPlayer
{
    private static final long INTERVAL = 1000L;
    private PlayStatus currentPlayStatus = PlayStatus.Stopped;
    private final Object playStatusLock = new Object();
    private AtomicInteger currentVolume = new AtomicInteger(100);
    private SubSystemSupport subSystemSupport;
    private AtomicInteger songProgress;
    private Playlist playlist;
    private Timer songTimer;

    public SimPlayer(SubSystemSupport subSystemSupport, Playlist playlist)
    {
        this.subSystemSupport = subSystemSupport;
        this.playlist = playlist;
        songProgress = new AtomicInteger(0);
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
            processPlayStatus(playStatus);
            subSystemSupport.updateSubSystemChanged(Subsystem.player);
        }
    }

    private void processPlayStatus(PlayStatus playStatus)
    {
        switch (playStatus)
        {
            case Playing:
                synchronized (this)
                {
                    if (songTimer == null)
                    {
                        songTimer = new Timer("SongTimer");
                        songTimer.scheduleAtFixedRate(new SongTimerTask(), INTERVAL, INTERVAL);
                    }
                }
                break;

            case Stopped:
                resetProgress();
            case Paused:
                synchronized (this)
                {
                    songTimer.cancel();
                    songTimer = null;
                }
                break;
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

    public void setSongProgress(int i)
    {
        songProgress.set(i);
    }

    public StatusTuple getTimeStatus()
    {
        String totalTime = getCurrentSongTotalTime();
        if (totalTime == null)
        {
            totalTime = BigInteger.ZERO.toString();
        }
        int currentTime = songProgress.get();
        return new StatusTuple(MpdStatus.time, currentTime + ":" + totalTime);
    }

    public void next()
    {
        resetProgress();
    }

    public void previous()
    {
        resetProgress();
    }

    private void resetProgress()
    {
        songProgress.set(0);
    }

    public void seek(Integer songId, Integer position)
    {
        playlist.gotoSongBySongId(songId);
        String currentSongTotalTime = getCurrentSongTotalTime();
        // todo: potential threading problem between get and set
        int progress = songProgress.get();
        if (currentSongTotalTime != null && (progress <= Integer.parseInt(currentSongTotalTime)))
        {
            Log.v(getClass().getSimpleName(), "seekid: " + position + ", previous=" + progress);
            songProgress.set(position);
            subSystemSupport.updateSubSystemChanged(Subsystem.player);
        }
        else
        {
            Log.d(getClass().getSimpleName(), "seekid: not seeking. total=" + currentSongTotalTime + ", progress=" + progress);
        }
    }

    private String getCurrentSongTotalTime()
    {
        return playlist.getCurrentSong().getValue(SongInfo.SongAttributeType.Time);
    }

    private class SongTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            int progress = songProgress.incrementAndGet();
            String totalTimeString = getCurrentSongTotalTime();
            if (totalTimeString != null && progress >= Integer.parseInt(totalTimeString))
            {
                // song finished
                resetProgress();
                playlist.next();
            }
            Log.v("SimPlayer", "timer fired: " + progress);
        }
    }
}
