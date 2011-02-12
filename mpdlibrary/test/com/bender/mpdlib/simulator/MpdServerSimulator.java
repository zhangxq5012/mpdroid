package com.bender.mpdlib.simulator;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class MpdServerSimulator
{
    public static final String VERSION = "sim v1.0";
    private SimPlayer simPlayer;
    private Playlist playlist;
    private SubSystemSupport subSystemSupport;
    private static final String TAG = MpdServerSimulator.class.getSimpleName();
    private static List<CommandStreamProvider> connections = new ArrayList<CommandStreamProvider>();
    private OptionsReg optionsReg;

    public MpdServerSimulator()
    {
        subSystemSupport = new SubSystemSupport();
        playlist = new Playlist(subSystemSupport);
        simPlayer = new SimPlayer(subSystemSupport, playlist);
        optionsReg = new OptionsReg(subSystemSupport);
    }

    public SocketStreamProviderIF createMpdSocket()
    {
        CommandStreamProvider commandStreamProvider = new CommandStreamProvider();
        connections.add(commandStreamProvider);
        return commandStreamProvider;
    }

    public SimPlayer getSimPlayer()
    {
        return simPlayer;
    }

    public Playlist getPlaylist()
    {
        return playlist;
    }

    public SubSystemSupport getSubSystemSupport()
    {
        return subSystemSupport;
    }

    public void crash()
    {
        for (CommandStreamProvider connection : connections)
        {
            try
            {
                connection.disconnect();
            } catch (IOException e)
            {
                Log.e(TAG, e);
            }
        }
        connections.clear();
    }

    private class CommandStreamProvider implements SocketStreamProviderIF
    {

        private boolean connected;
        private ServerThread serverThread;

        private PipedReader clientPipedReader;
        private PrintWriter simBufferedWriter;
        private BufferedReader simBufferedReader;
        private PipedWriter clientPipedWriter;

        public void connect(SocketAddress socketAddress) throws IOException
        {
            Log.v(TAG, "connect(addr=" + socketAddress + ")");
            clientPipedReader = new PipedReader();
            PipedWriter simPipedWriter = new PipedWriter(clientPipedReader);
            simBufferedWriter = new PrintWriter(simPipedWriter, true);
            PipedReader simPipedReader = new PipedReader();
            simBufferedReader = new BufferedReader(simPipedReader);
            clientPipedWriter = new PipedWriter(simPipedReader);
            connected = true;
            serverThread = new ServerThread(new SocketStreamProviderIF()
            {
                public void connect(SocketAddress socketAddress) throws IOException
                {
                }

                public BufferedReader getBufferedReader() throws IOException
                {
                    return simBufferedReader;
                }

                public PrintWriter getPrintWriter() throws IOException
                {
                    return simBufferedWriter;
                }

                public void disconnect() throws IOException
                {
                }

                public boolean isConnected()
                {
                    return CommandStreamProvider.this.isConnected();
                }
            }, playlist, simPlayer, subSystemSupport, optionsReg);
            serverThread.start();
            Log.v(TAG, "SIM connect()");
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            return new BufferedReader(clientPipedReader);
        }

        public PrintWriter getPrintWriter() throws IOException
        {
            return new PrintWriter(clientPipedWriter, true);
        }

        public void disconnect() throws IOException
        {
            connected = false;
            if (clientPipedReader != null)
            {
                clientPipedReader.close();
            }
            if (clientPipedWriter != null)
            {
                clientPipedWriter.close();
            }
            if (simBufferedWriter != null)
            {
                simBufferedWriter.close();
            }
            if (simBufferedReader != null)
            {
                simBufferedReader.close();
            }
            if (serverThread != null)
            {
                serverThread.interrupt();
            }
            Log.v(TAG, "disconnect()");
        }

        public boolean isConnected()
        {
            return connected;
        }
    }


    public static void main(String[] args) throws Exception
    {
        if (args.length == 1)
        {
            startServer(args);
            return;
        }
        MpdServerSimulator mpdServerSimulator = new MpdServerSimulator();
        SocketStreamProviderIF mpdSocket = mpdServerSimulator.createMpdSocket();

        mpdSocket.connect(null);
        final PrintWriter bufferedWriter = mpdSocket.getPrintWriter();
        final BufferedReader bufferedReader = mpdSocket.getBufferedReader();
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        System.err.println(line);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        Runnable runnable = new Runnable()
        {
            BufferedReader reader;

            public void run()
            {
                reader = new BufferedReader(new InputStreamReader(System.in));
                String line;
                try
                {
                    while ((line = reader.readLine()) != null)
                    {
                        bufferedWriter.println(line);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        };
        Thread readThread = new Thread(runnable);
        readThread.start();

        thread.join();
        System.exit(0);
    }

    private static void startServer(String[] args) throws IOException
    {
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        SubSystemSupport systemSupport = new SubSystemSupport();
        Playlist playlist = new Playlist(systemSupport);
        SimPlayer simPlayer = new SimPlayer(systemSupport, playlist);
        OptionsReg optionsReg = new OptionsReg(systemSupport);

        while (true)
        {
            final Socket clientSocket = serverSocket.accept();
            Log.v(TAG, "client connected" + clientSocket.getInetAddress());
            final ServerThread serverThread = new ServerThread(new SocketStreamProviderIF()
            {
                public void connect(SocketAddress socketAddress) throws IOException
                {
                }

                public BufferedReader getBufferedReader() throws IOException
                {
                    return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                }

                public PrintWriter getPrintWriter() throws IOException
                {
                    return new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
                }

                public void disconnect() throws IOException
                {
                    clientSocket.close();
                }

                public boolean isConnected()
                {
                    return clientSocket.isConnected() && !clientSocket.isClosed();
                }
            }, playlist, simPlayer, systemSupport, optionsReg);
            serverThread.start();
        }
    }

}
