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
}
