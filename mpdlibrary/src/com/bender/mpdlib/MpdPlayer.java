package com.bender.mpdlib;

import com.bender.mpdlib.commands.*;

import java.util.List;

/**
 * todo: replace with documentation
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

    public MpdPlayer(Pipe commandPipe)
    {
        this.commandPipe = commandPipe;
    }

    public void play()
    {
        System.out.println(TAG + ": play()");
        CommandRunner.runCommand(new PlayCommand(commandPipe));
    }


    public synchronized PlayStatus getPlayStatus()
    {
        return playState;
    }

    public void stop()
    {
        System.out.println(TAG + ": stop()");
        CommandRunner.runCommand(new StopCommand(commandPipe));
    }

    public void next()
    {
        System.out.println(TAG + ": next()");
        CommandRunner.runCommand(new NextCommand(commandPipe));
    }

    public void pause()
    {
        System.out.println(TAG + ": pause()");
        CommandRunner.runCommand(new PauseCommand(commandPipe));
    }

    public void addPlayStatusListener(PlayStatusListener listener)
    {
        myListener = listener;
    }

    public void previous()
    {
        System.out.println(TAG + ": previous()");
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
            }
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
            songId = newSongId;
            System.out.println(TAG + ": GetCurrentSongCommand");
            Result<SongInfo> result = CommandRunner.runCommand(new GetCurrentSongCommand(commandPipe));
            if (result.status.isSuccessful())
            {
                currentSongInfo = result.result;
                currentSongListener.songUpdated(result.result);
                System.out.println(TAG + ": songUpdated(): " + result.result.getValue(SongInfo.SongAttributeType.Id));
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
            System.out.println(TAG + ": playStatusChanged: " + newPlayStatus);
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
            System.out.println(getClass().getSimpleName() + "playStatusChanged()" + playStatus);
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
}
