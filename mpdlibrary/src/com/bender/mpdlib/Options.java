package com.bender.mpdlib;

import com.bender.mpdlib.commands.RandomCommand;
import com.bender.mpdlib.commands.RepeatCommand;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.util.Log;

import java.util.Map;

/**
 */
public class Options
{
    private static final String TAG = Options.class.getSimpleName();

    private Pipe commandPipe;
    private boolean repeat = false;
    private OptionsListener optionsListener = new NullOptionsListener();
    private boolean random = false;

    public Options(Pipe commandPipe)
    {
        this.commandPipe = commandPipe;
    }

    public Boolean getRepeat()
    {
        return repeat;
    }

    public void toggleRepeat()
    {
        CommandRunner.runCommand(new RepeatCommand(commandPipe, !repeat));
    }

    public void processStatus(Map<MpdStatus, StatusTuple> statusTupleMap)
    {
        if (statusTupleMap.containsKey(MpdStatus.repeat))
        {
            repeatUpdated(statusTupleMap.get(MpdStatus.repeat));
        }
        if (statusTupleMap.containsKey(MpdStatus.random))
        {
            randomUpdated(statusTupleMap.get(MpdStatus.random));
        }
    }

    private void randomUpdated(StatusTuple statusTuple)
    {
        String value = statusTuple.getValue();
        boolean newRandom = MpdBoolean.parseString(value);
        boolean changed;
        synchronized (this)
        {
            changed = newRandom != random;
        }
        random = newRandom;
        if (changed)
        {
            Log.v(TAG, "random updated: " + newRandom);
            optionsListener.randomUpdated(newRandom);
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
            optionsListener.repeatUpdated(newRepeat);
        }
    }

    public void setListener(OptionsListener optionsListener)
    {
        this.optionsListener = optionsListener;
    }

    public void toggleRandom()
    {
        CommandRunner.runCommand(new RandomCommand(commandPipe, new MpdBoolean(!random)));
    }

    public Boolean getRandom()
    {
        return random;
    }

    private static class NullOptionsListener implements OptionsListener
    {
        public void repeatUpdated(boolean repeat)
        {
        }

        public void randomUpdated(boolean newRandom)
        {
        }
    }
}
