package com.bender.mpdlib.simulator;

import com.bender.mpdlib.MpdBoolean;
import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 */
public class OptionsReg
{
    private SubSystemSupport subSystemSupport;

    private AtomicBoolean repeat = new AtomicBoolean(false);
    private AtomicBoolean random = new AtomicBoolean(false);

    public OptionsReg(SubSystemSupport subSystemSupport)
    {
        this.subSystemSupport = subSystemSupport;
    }

    public void setRepeat(boolean repeat)
    {
        this.repeat.set(repeat);
        // todo: update playlist
        subSystemSupport.updateSubSystemChanged(Subsystem.options);
    }

    public void setRandom(boolean random)
    {
        this.random.set(random);
        // todo: update playlist
        subSystemSupport.updateSubSystemChanged(Subsystem.options);
    }

    public List<StatusTuple> getStatusList()
    {
        ArrayList<StatusTuple> statusTuples = new ArrayList<StatusTuple>();
        statusTuples.add(new StatusTuple(MpdStatus.repeat, MpdBoolean.toString(repeat.get())));
        statusTuples.add(new StatusTuple(MpdStatus.random, MpdBoolean.toString(random.get())));
        return statusTuples;
    }
}
