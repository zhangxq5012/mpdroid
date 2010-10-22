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

    public MpdPlayer(Pipe commandPipe)
    {
        this.commandPipe = commandPipe;
    }

    public void play()
    {
        try
        {
            CommandRunner.runCommand(new PlayCommand(commandPipe));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public PlayStatus getPlayStatus()
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

    void processStatus(List<StatusTuple> statusTupleList)
    {
        for (StatusTuple statusTuple : statusTupleList)
        {
            switch (statusTuple.first())
            {
                case state:
                    PlayStatus newPlayStatus = PlayStatus.parse(statusTuple.second());
                    boolean changed = !newPlayStatus.equals(playState);
                    if (changed)
                    {
                        playState = newPlayStatus;
                        myListener.playStatusChanged();
                    }
                    break;
                case songid:
                    Integer newSongId = Integer.parseInt(statusTuple.getValue());
                    if (!newSongId.equals(songId))
                    {
                        songId = newSongId;
                        Result<SongInfo> result = CommandRunner.runCommand(new GetCurrentSongCommand(commandPipe));
                        if (result.status.isSuccessful())
                        {
                            currentSongInfo = result.result;
                            currentSongListener.songUpdated(currentSongInfo);
                        }
                    }
                    break;
            }
        }
    }

    private class NullPlayStatusListener implements PlayStatusListener
    {
        public void playStatusChanged()
        {
            System.out.println(getClass().getSimpleName() + "playStatusChanged()");
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
