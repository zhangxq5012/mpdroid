package com.bender.mpdlib.commands;

import com.bender.mpdlib.Pipe;

import java.io.IOException;

/**
 * todo: replace with documentation
 */
public class IdleCommand extends Command<Result<String[]>>
{
    private String[] strings;

    public IdleCommand(Pipe pipe, String[] strings)
    {
        super(pipe);
        this.strings = strings;
    }

    @Override
    public void executeCommand() throws IOException
    {
        StringBuffer arguments = new StringBuffer();
        for (String string : strings)
        {
            arguments.append(" ").append(string);
        }
        pipe.write(MpdCommands.idle.toString() + arguments.toString());
    }

    @Override
    public Result<String[]> readResult() throws IOException
    {
        String changedLine = pipe.readLine();
        String okLine = pipe.readLine();
        Status status = Status.parse(okLine);
        Result<String[]> result = new Result<String[]>();
        result.status = status;
        result.result = new String[]{changedLine};
        return result;
    }
}
