package com.bender.mpdlib.simulator;

import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.simulator.commands.*;
import com.bender.mpdlib.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * todo: replace with documentation
 */
public class SimCommandFactory
{

    public SimCommandFactory()
    {
    }

    public SimCommand create(MpdCommands command)
    {
        return SimCommands.construct(command);
    }

    //todo: refactor to avoid needing to specify the mapping
    private static enum SimCommands
    {
        Idle(MpdCommands.idle, IdleSimCommand.class),
        CurrentSong(MpdCommands.currentsong, CurrentSongSimCommand.class),
        Volume(MpdCommands.setvol, VolumeSimCommand.class),
        Next(MpdCommands.next, NextSimCommand.class),
        Status(MpdCommands.status, StatusSimCommand.class),
        Play(MpdCommands.play, PlaySimSimCommand.class),
        Pause(MpdCommands.pause, PauseSimCommand.class),
        Previous(MpdCommands.previous, PreviousSimCommand.class),
        Stop(MpdCommands.stop, StopSimCommand.class),
        Seek(MpdCommands.seekid, SeekByIdSimCommand.class),
        Close(MpdCommands.close, CloseSimCommand.class),
        Repeat(MpdCommands.repeat, RepeatSimCommand.class),
        Random(MpdCommands.random, RandomSimCommand.class);

        private MpdCommands command;
        private Class<? extends SimCommand> simCommandClass;
        private static final String TAG = SimCommand.class.getSimpleName();

        SimCommands(MpdCommands mpdCommands, Class<? extends SimCommand> simCommandClass)
        {
            command = mpdCommands;
            this.simCommandClass = simCommandClass;
        }

        static SimCommand construct(MpdCommands mpdCommands)
        {
            for (SimCommands simCommands : values())
            {
                if (simCommands.command.equals(mpdCommands))
                {
                    return newInstance(simCommands.simCommandClass);
                }
            }
            return null;
        }

        private static SimCommand newInstance(Class<? extends SimCommand> simCommandClass)
        {
            try
            {
                Constructor<? extends SimCommand> constructor = simCommandClass.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e)
            {
                Log.e(TAG, e);
            } catch (InvocationTargetException e)
            {
                Log.e(TAG, e);
            } catch (InstantiationException e)
            {
                Log.e(TAG, e);
            } catch (IllegalAccessException e)
            {
                Log.e(TAG, e);
            }
            return null;
        }
    }
}
