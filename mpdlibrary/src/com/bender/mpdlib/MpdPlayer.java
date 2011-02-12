package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;
import com.bender.mpdlib.util.Log;

import java.util.*;

/**
 */
class MpdPlayer implements Player
{
    private Pipe commandPipe;
    private PlayStatus playState;
    private Set<PlayStatusListener> playStatusListeners = new HashSet<PlayStatusListener>();
    private SongInfo currentSongInfo = new NullSongInfo();
    private Integer songId;
    private CurrentSongListener currentSongListener = new NullCurrentSongListener();
    private static final String TAG = MpdPlayer.class.getSimpleName();
    private Integer volume;
    private VolumeListener volumeListener = new NullVolumeListener();
    private boolean muted;
    private SongProgressListener songProgressListener = new NullSongProgressListener();
    private SongTimerManager songTimerManager;

    private boolean repeat;

    public MpdPlayer(Pipe commandPipe)
    {
        this.commandPipe = commandPipe;
        songTimerManager = new SongTimerManager();
    }

    public void play()
    {
        CommandRunner.runCommand(new PlayCommand(commandPipe));
    }


    public synchronized PlayStatus getPlayStatus()
    {
        return playState;
    }

    public void stop()
    {
        CommandRunner.runCommand(new StopCommand(commandPipe));
    }

    public void next()
    {
        CommandRunner.runCommand(new NextCommand(commandPipe));
    }

    public void pause()
    {
        CommandRunner.runCommand(new PauseCommand(commandPipe));
    }

    public void addPlayStatusListener(PlayStatusListener listener)
    {
        playStatusListeners.add(listener);
    }

    public void previous()
    {
        CommandRunner.runCommand(new PreviousCommand(commandPipe));
    }

    public SongInfo getCurrentSongInfo()
    {
        return currentSongInfo;
    }

    public void addCurrentSongListener(CurrentSongListener currentSongListener)
    {
        this.currentSongListener = currentSongListener;
    }

    public Integer getVolume()
    {
        return volume;
    }

    public void setVolume(Integer volume)
    {
        setVolumeImpl(volume);
    }

    public void setVolumeImpl(Integer volume)
    {
        CommandRunner.runCommand(new SetVolumeCommand(commandPipe, volume));
    }

    public void addVolumeListener(VolumeListener listener)
    {
        volumeListener = listener;
    }

    public Boolean toggleMute()
    {
        boolean muted = isMuted();
        if (muted)
        {
            //todo: use last cached volume
            setVolumeImpl(100);
        } else
        {
            setVolumeImpl(0);
        }
        return null;
    }

    public boolean isMuted()
    {
        return muted;
    }

    public SongProgress getProgress()
    {
        return songTimerManager.getProgress();
    }

    public void addSongProgressListener(SongProgressListener listener)
    {
        songProgressListener = listener;
    }

    public void seek(Integer position)
    {
        SeekArg seekArg = new SeekArg(songId, position);
        CommandRunner.runCommand(new SeekCommand(commandPipe, seekArg));
    }

    public void toggleRepeat()
    {
        CommandRunner.runCommand(new RepeatCommand(commandPipe, !repeat));
    }

    void processStatus(Map<MpdStatus, StatusTuple> statusTupleMap)
    {

        if (statusTupleMap.containsKey(MpdStatus.state))
        {
            stateUpdated(statusTupleMap.get(MpdStatus.state));
        }

        if (statusTupleMap.containsKey(MpdStatus.songid))
        {
            songUpdated(statusTupleMap.get(MpdStatus.songid));
        }

        if (statusTupleMap.containsKey(MpdStatus.volume))
        {
            volumeUpdated(statusTupleMap.get(MpdStatus.volume));
        }

        if (statusTupleMap.containsKey(MpdStatus.time))
        {
            timeUpdated(statusTupleMap.get(MpdStatus.time));
        }

        if (statusTupleMap.containsKey(MpdStatus.repeat))
        {
            repeatUpdated(statusTupleMap.get(MpdStatus.repeat));
        }

    }

    private void repeatUpdated(StatusTuple statusTuple)
    {
        String value = statusTuple.getValue();
        boolean newRepeat = MpdBoolean.parseString(value);
        boolean changed;
        synchronized (this)
        {
            changed = newRepeat != repeat;
        }
        repeat = newRepeat;
        if (changed)
        {
            Log.v(TAG, "repeat updated: " + newRepeat);
        }
    }

    private void timeUpdated(StatusTuple statusTuple)
    {
        String value = statusTuple.getValue();
        String[] splitStrings = value.split(":");
        SongProgress progress;
        if (splitStrings.length == 2)
        {
            int currentTime = Integer.parseInt(splitStrings[0].trim());
            int totalTime = Integer.parseInt(splitStrings[1].trim());
            progress = new SongProgress(currentTime, totalTime);
            Log.v(TAG, "time updated: " + progress);
        } else
        {
            Log.w(TAG, "Illegal time value: " + value);
            progress = new NullProgress();
        }
        songTimerManager.setSongProgress(progress);
    }

    private void volumeUpdated(StatusTuple statusTuple)
    {
        Integer newVolume = Integer.valueOf(statusTuple.getValue());
        boolean changed;
        synchronized (this)
        {
            changed = !newVolume.equals(volume);
        }
        volume = newVolume;
        if (changed)
        {
            muted = newVolume.equals(0);
            Log.i(TAG, "volumeUpdated(): " + newVolume + ", " + (muted ? "muted" : ""));
            volumeListener.volumeChanged(newVolume);
        }
    }

    private void songUpdated(StatusTuple statusTuple)
    {
        Integer newSongId = Integer.parseInt(statusTuple.getValue());
        boolean changed;
        synchronized (this)
        {
            changed = !newSongId.equals(songId);
            songId = newSongId;
        }
        if (changed)
        {
            Result<SongInfo> result = CommandRunner.runCommand(new GetCurrentSongCommand(commandPipe));
            if (result.status.isSuccessful())
            {
                currentSongInfo = result.result;
                currentSongListener.songUpdated(result.result);
                Log.i(TAG, "songUpdated(): " + result.result.getValue(SongInfo.SongAttributeType.Id));
            }
        }
    }

    private void stateUpdated(StatusTuple statusTuple)
    {
        PlayStatus newPlayStatus = PlayStatus.parse(statusTuple.second());
        boolean changed;
        synchronized (this)
        {
            changed = !newPlayStatus.equals(playState);
            playState = newPlayStatus;
        }
        if (changed)
        {
            songTimerManager.updateSongTimer(newPlayStatus);
            for (PlayStatusListener playStatusListener : playStatusListeners)
            {
                playStatusListener.playStatusChanged(newPlayStatus);
            }
            Log.i(TAG, "playStatusChanged(): " + newPlayStatus);
        }
    }


    private class SongTimerManager
    {
        private Timer songTimer;
        private SongProgress progress = new NullProgress();

        //todo: replace with better sync
        private synchronized void updateSongTimer(PlayStatus newPlayStatus)
        {
            switch (newPlayStatus)
            {
                case Playing:
                    if (songTimer == null)
                    {
                        songTimer = new Timer("SongTimer");
                        songTimer.scheduleAtFixedRate(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                if (progress.isDone())
                                {
                                    songTimer.cancel();
                                    songTimer = null;
                                } else
                                {
                                    progress.increment();
                                    songProgressListener.songProgressUpdated(progress);
                                }
                            }
                        }, 1000L, 1000L);
                    } else
                    {
                        Log.w(TAG, "updateSongTimer: Playing is status but there is an existing songTimer");
                    }
                    break;
                case Stopped:
                    progress = new NullProgress();
                    songProgressListener.songProgressUpdated(progress);
                case Paused:
                    if (songTimer != null)
                    {
                        songTimer.cancel();
                        songTimer = null;
                    } else
                    {
                        Log.w(TAG, "updateSongTimer: " + newPlayStatus + " is status but there is no songTimer");
                    }
                    break;
            }
        }

        public synchronized SongProgress getProgress()
        {
            return progress;
        }

        public synchronized void setSongProgress(SongProgress progress)
        {
            this.progress = progress;
            songProgressListener.songProgressUpdated(progress);
        }

        public synchronized void disconnect()
        {
            if (songTimer != null)
            {
                songTimer.cancel();
                songTimer = null;
                progress = new NullProgress();
            }
        }
    }

    public void disconnect()
    {
        playStatusListeners.clear();
        currentSongListener = new NullCurrentSongListener();
        songTimerManager.disconnect();
    }

    private static class NullSongProgressListener implements SongProgressListener
    {
        public void songProgressUpdated(SongProgress songProgress)
        {
        }
    }

    private class NullSongInfo extends SongInfo
    {
        @Override
        public String getValue(SongAttributeType songAttributeType)
        {
            return "";
        }
    }

    private class NullCurrentSongListener implements CurrentSongListener
    {
        public void songUpdated(SongInfo songInfo)
        {
        }
    }

    private class NullVolumeListener implements VolumeListener
    {
        public void volumeChanged(Integer volume)
        {
        }
    }

    private class NullProgress extends SongProgress
    {
        private NullProgress()
        {
            super(null, null);
        }

        @Override
        void increment()
        {
        }
    }
}
