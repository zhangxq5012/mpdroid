package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 */
public class PlaySongIdCommand extends StatusCommand<SingleArg<Integer>> {
    public PlaySongIdCommand(Pipe commandPipe, int id) {
        super(commandPipe, new SingleArg<Integer>(id));
    }

    @Override
    protected void executeCommand(SingleArg<Integer> arg) throws IOException {
        pipe.write(MpdCommands.playid + " " + arg);
    }
}
