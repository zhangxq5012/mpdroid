package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;
import com.bender.mpdlib.Subsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class IdleCommand extends Command<SingleArg<IdleCommand.SubsystemList>, Result<List<Subsystem>>> {
    public IdleCommand(Pipe pipe) {
        this(pipe, new SubsystemList(new Subsystem[]{}));
    }

    public IdleCommand(Pipe pipe, SubsystemList subsystems) {
        super(pipe, new SingleArg<SubsystemList>(subsystems));
    }

    @Override
    protected void executeCommand(SingleArg<SubsystemList> arg) throws IOException {
        StringBuffer arguments = new StringBuffer("");
        for (Subsystem subsystem : arg.getArg().subsystems) {
            arguments.append(" ").append(subsystem.toString());
        }
        pipe.write(MpdCommands.idle.toString() + arguments.toString());
    }

    @Override
    protected Result<List<Subsystem>> readResult() throws IOException {
        String line;
        List<Subsystem> ret = new ArrayList<Subsystem>();
        while (!Response.isResponseLine(line = pipe.readLine())) {
            if (line == null) break;
            Subsystem subsystem = Subsystem.parse(line);
            ret.add(subsystem);
        }
        Status status = Status.parse(line);
        Result<List<Subsystem>> result = new Result<List<Subsystem>>();
        result.status = status;
        result.result = ret;
        return result;
    }

    static class SubsystemList {
        public final Subsystem[] subsystems;

        public SubsystemList(Subsystem[] subsystems) {
            this.subsystems = subsystems;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (subsystems.length == 0) {
                return null;
            }
            for (Subsystem subsystem : subsystems) {
                stringBuilder.append(subsystem);
            }
            return stringBuilder.toString();
        }
    }
}
