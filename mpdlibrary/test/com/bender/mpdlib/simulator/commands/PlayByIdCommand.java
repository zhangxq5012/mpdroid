package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.commands.Response;

/**
 */
public class PlayByIdCommand extends PlayerSimCommand {
    @Override
    public void runWithArgs(String[] commands) {
        if (commands.length != 2) {
            printWriter.println(Response.ACK + " [2@0] {playid} wrong number of args");
            return;
        }
        int id = Integer.parseInt(commands[1]);
        playlist.gotoSongBySongId(id);
        simPlayer.resetProgress();
    }

    @Override
    protected PlayStatus getPlayStatus() {
        return PlayStatus.Playing;
    }
}
