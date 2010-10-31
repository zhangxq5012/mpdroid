package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;
import com.bender.mpdlib.util.Log;

import java.util.List;

/**
 */
class MpdPlayer implements Player
{
    private Pipe commandPipe;
    private PlayStatus playState;
    private PlayStatusListener myListener = new NullPlayStatusListener();
    private SongInfo currentSongInfo = new NullSongInfo();
    private Integer songId;
    private CurrentSongListener currentSongListener = new NullCurrentSongListener();
    private static final String TAG = MpdPlayer.class.getSimpleName();
    private Integer volume;
    private VolumeListener volumeListener = new NullVolumeListener();
    private boolean muted;

    public MpdPlayer(Pipe commandPipe)
    {
        this.commandPipe = commandPipe;
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
        myListener = listener;
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
        }
        else
        {
            setVolumeImpl(0);
        }
        return null;
    }

    public boolean isMuted()
    {
        return muted;
    }

    void processStatus(List<StatusTuple> statusTupleList)
    {
        for (StatusTuple statusTuple : statusTupleList)
        {
            switch (statusTuple.first())
            {
                case state:
                    stateUpdated(statusTuple);
                    break;
                case songid:
                    songUpdated(statusTuple);
                    break;
                case volume:
                    volumeUpdated(statusTuple);
            }
        }
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
            myListener.playStatusChanged(newPlayStatus);
            Log.i(TAG, "playStatusChanged(): " + newPlayStatus);
        }
    }

    public void disconnect()
    {
        myListener = new NullPlayStatusListener();
        currentSongListener = new NullCurrentSongListener();
    }

    private class NullPlayStatusListener implements PlayStatusListener
    {
        public void playStatusChanged(PlayStatus playStatus)
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
}
