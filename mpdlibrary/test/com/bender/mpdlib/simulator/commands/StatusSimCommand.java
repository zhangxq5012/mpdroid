package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 */
public class StatusSimCommand extends SimCommand {

    public void run(String[] commands) throws Exception {
        List<StatusTuple> statusTupleList = simPlayer.getStatusList();
        addStatuses(statusTupleList);

        statusTupleList = playlist.getStatusList();
        addStatuses(statusTupleList);

        statusTupleList = optionsReg.getStatusList();
        addStatuses(statusTupleList);

        printWriter.println(Response.OK);
    }

    private void addStatuses(List<StatusTuple> statusTupleList) throws IOException {
        for (StatusTuple statusTuple : statusTupleList) {
            write(printWriter, statusTuple);
        }
    }

    private void write(PrintWriter printWriter, StatusTuple statusTuple) throws IOException {
        if (statusTuple != null) {
            String string = statusTuple.first() + ": " + statusTuple.second();
            Log.v(getClass().getSimpleName(), "   " + string);
            printWriter.println(string);
        }
    }
}
