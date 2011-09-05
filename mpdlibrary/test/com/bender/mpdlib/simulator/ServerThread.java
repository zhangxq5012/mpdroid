package com.bender.mpdlib.simulator;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.commands.CloseSimCommand;
import com.bender.mpdlib.simulator.commands.CommandResourceIF;
import com.bender.mpdlib.simulator.commands.SimCommand;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 */
class ServerThread extends Thread implements CommandResourceIF {
    private SocketStreamProviderIF provider;
    private PrintWriter printWriter;
    private BufferedReader simBufferedReader;
    private static int count;
    private Playlist playlist;
    private SimPlayer simPlayer;
    private SubSystemSupport subSystemSupport;
    private static final String TAG = "MpdServerThread";
    private SimCommandFactory simCommandFactory;
    private OptionsReg optionsReg;

    public ServerThread(SocketStreamProviderIF commandStreamProvider, Playlist playlist, SimPlayer simPlayer, SubSystemSupport subSystemSupport, OptionsReg optionsReg) {
        super("Sim-" + count++);
        this.playlist = playlist;
        this.simPlayer = simPlayer;
        this.subSystemSupport = subSystemSupport;
        this.optionsReg = optionsReg;
        provider = commandStreamProvider;
        simCommandFactory = new SimCommandFactory();
        try {
            simBufferedReader = provider.getBufferedReader();
            printWriter = provider.getPrintWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (connected()) {
            printWriter.println(Response.OK + " " + MpdServerSimulator.VERSION);
        }
        while (connected()) {
            String line = null;
            try {
                line = simBufferedReader.readLine();
            } catch (IOException e) {
                Log.e(TAG, e);
            }
            if (!connected() || line == null) {
                return;
            }
            try {
                process(line);
            } catch (IOException e) {
                Log.e(TAG, e);
            }
        }
    }

    private void process(String line) throws IOException {
        String[] strings = line.split("\\s");
        // this is kind of hack.
        strings = processQuotes(strings);
        String commandString = strings[0].trim();
        MpdCommands command = MpdCommands.parse(commandString);
        if (command == null) {
            printWriter.println(Response.ACK + " [5@0] {} unknown command \"" + commandString + "\"");
            return;
        }
        Log.v(TAG, "process: " + command);
        try {
            SimCommand simCommand = simCommandFactory.create(command);
            if (simCommand == null) {
                printWriter.println(Response.ACK + "[5@0] Unimplemented method: " + command);
            } else {
                runCommand(strings, simCommand);
            }
        } catch (Exception e) {
            Log.e(TAG, e);
            printWriter.println(Response.ACK + "[5@0] Unhandled Exception");
        }
        Log.v(TAG, command + " DONE");
    }

    // this is soo bad =/  it 'parses' double quotes as a single argument
    private String[] processQuotes(String[] strings) {
        boolean foundQuote = false;
        List<String> result = new ArrayList<String>(strings.length);
        StringBuffer buf = null;
        for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
            String string = strings[i];
            if (string.charAt(0) == '"') {
                foundQuote = true;
                buf = new StringBuffer();
                buf.append(string.substring(1));
            } else if (foundQuote && string.charAt(string.length() - 1) == '"') {
                // end quote
                foundQuote = false;
                buf.append(' ').append(string.substring(0, string.length() - 1));
                result.add(buf.toString());
            } else if (foundQuote) {
                buf.append(' ').append(string);
            } else {
                result.add(string);
            }
        }
        return result.toArray(new String[result.size()]);
    }

    private void runCommand(String[] strings, SimCommand simCommand)
            throws Exception {
        simCommand.execute(strings, this);
    }

    private boolean connected() {
        return provider.isConnected() || !CloseSimCommand.isClosed();
    }

    public SocketStreamProviderIF getProvider() {
        return provider;
    }

    public OptionsReg getOptionsReg() {
        return optionsReg;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public SimPlayer getSimPlayer() {
        return simPlayer;
    }

    public SubSystemSupport getSubSystemSupport() {
        return subSystemSupport;
    }
}
