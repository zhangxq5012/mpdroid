package com.bender.mpdlib;

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

    private static class NullOptionsListener implements OptionsListener
    {
        public void repeatUpdated(boolean repeat)
        {
        }
    }
}
