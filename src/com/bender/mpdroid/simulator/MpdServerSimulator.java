package com.bender.mpdroid.simulator;

import android.util.Log;
import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;

import java.io.*;
import java.net.SocketAddress;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class MpdServerSimulator
{
    private CommandStreamProvider commandStreamProviderIF;

    private PipedReader clientPipedReader;
    private BufferedWriter simBufferedWriter;
    private BufferedReader simBufferedReader;
    private PipedWriter clientPipedWriter;

    private static final String VERSION = "sim v1.0";
    private static final String TAG = MpdServerSimulator.class.getSimpleName();

    public SocketStreamProviderIF createMpdSocket()
    {
        commandStreamProviderIF = new CommandStreamProvider();
        return commandStreamProviderIF;
    }

    private void process(String line) throws IOException
    {
        StringTokenizer stringTokenizer = new StringTokenizer(line);
        MpdCommands command = MpdCommands.parse(stringTokenizer.nextToken());
        Log.v(TAG, "process(): " + command);
        writeLine(Response.OK.toString());
    }

    private void writeLine(String s) throws IOException
    {
        simBufferedWriter.write(s);
        simBufferedWriter.newLine();
        simBufferedWriter.flush();
    }

    private class CommandStreamProvider implements SocketStreamProviderIF
    {

        private boolean connected;
        private ServerThread serverThread;

        public void connect(SocketAddress socketAddress) throws IOException
        {
            clientPipedReader = new PipedReader();
            PipedWriter simPipedWriter = new PipedWriter(clientPipedReader);
            simBufferedWriter = new BufferedWriter(simPipedWriter);
            PipedReader simPipedReader = new PipedReader();
            simBufferedReader = new BufferedReader(simPipedReader);
            clientPipedWriter = new PipedWriter(simPipedReader);
            connected = true;
            serverThread = new ServerThread();
            serverThread.start();
            Log.v(TAG, "connect()");
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            return new BufferedReader(clientPipedReader);
        }

        public BufferedWriter getBufferedWriter() throws IOException
        {
            return new BufferedWriter(clientPipedWriter);
        }

        public void disconnect() throws IOException
        {
            connected = false;
            serverThread.interrupt();
            Log.v(TAG, "disconnect()");
        }

        public boolean isConnected()
        {
            return connected;
        }
    }

    private class ServerThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                simBufferedWriter.write(Response.OK + " " + VERSION);
                while (connected())
                {
                    String line = simBufferedReader.readLine();
                    if (!connected())
                    {
                        return;
                    }
                    process(line);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        private boolean connected()
        {
            return commandStreamProviderIF.connected;
        }
    }

}
