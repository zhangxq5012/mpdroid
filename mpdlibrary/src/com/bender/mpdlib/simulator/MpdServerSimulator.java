package com.bender.mpdlib.simulator;

import com.bender.mpdlib.SocketStreamProviderIF;
import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.simulator.commands.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class MpdServerSimulator
{

    private static final String VERSION = "sim v1.0";

    public SocketStreamProviderIF createMpdSocket()
    {
        return new CommandStreamProvider();
    }


    private class CommandStreamProvider implements SocketStreamProviderIF
    {

        private boolean connected;
        private ServerThread serverThread;

        private PipedReader clientPipedReader;
        private BufferedWriter simBufferedWriter;
        private BufferedReader simBufferedReader;
        private PipedWriter clientPipedWriter;

        public void connect(SocketAddress socketAddress) throws IOException
        {
            System.out.println("connect(addr=" + socketAddress + ")");
            clientPipedReader = new PipedReader();
            PipedWriter simPipedWriter = new PipedWriter(clientPipedReader);
            simBufferedWriter = new BufferedWriter(simPipedWriter)
            {
                @Override
                public void write(String s) throws IOException
                {
                    super.write(s);
                    if (!s.equals("\n"))
                    {
                        newLine();
                        flush();
                    }
                }
            };
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

                public BufferedWriter getBufferedWriter() throws IOException
                {
                    return simBufferedWriter;
                }

                public void disconnect() throws IOException
                {
                }

                public boolean isConnected()
                {
                    return false;  //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            serverThread.start();
            System.out.println("connect()");
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
            clientPipedReader.close();
            clientPipedWriter.close();
            simBufferedWriter.close();
            simBufferedReader.close();
            System.out.println("disconnect()");
        }

        public boolean isConnected()
        {
            return connected;
        }
    }

    private static class ServerThread extends Thread
    {
        private SocketStreamProviderIF provider;
        private PrintWriter printWriter;
        private BufferedReader simBufferedReader;
        private static int count;

        public ServerThread(SocketStreamProviderIF commandStreamProvider)
        {
            super("Sim-" + count++ + ":");
            provider = commandStreamProvider;
            try
            {
                simBufferedReader = provider.getBufferedReader();
                printWriter = new PrintWriter(provider.getBufferedWriter(), true);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            if (connected())
            {
                printWriter.println(Response.OK + " " + VERSION);
            }
            while (connected())
            {
                try
                {
                    String line = simBufferedReader.readLine();
                    if (!connected())
                    {
                        return;
                    }
                    process(line);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void process(String line) throws IOException
        {
            StringTokenizer stringTokenizer = new StringTokenizer(line);
            String commandString = stringTokenizer.nextToken().trim();
            MpdCommands command = MpdCommands.parse(commandString);
            if (command == null)
            {
                printWriter.println(Response.ACK + " [5@0] {} unknown command \"" + commandString + "\"");
                return;
            }
            System.out.println(getName() + "process(): " + command);
            switch (command)
            {
                case close:
                    provider.disconnect();
                    break;
                case idle:
                    IdleSimCommand idleSimCommand = new IdleSimCommand(printWriter);
                    idleSimCommand.run();
                    break;
                case status:
                    runSimCommand(new StatusSimCommand(printWriter));
                    break;
                case play:
                    PlaySimSimCommand playSimCommand = new PlaySimSimCommand(printWriter);
                    playSimCommand.run();
                    break;
                case pause:
                    PauseSimCommand pauseSimCommand = new PauseSimCommand(printWriter);
                    pauseSimCommand.run();
                    break;
                case stop:
                    StopSimCommand stopSimCommand = new StopSimCommand(printWriter);
                    stopSimCommand.run();
                    break;
                case next:
                    runSimCommand(new NextSimCommand(printWriter));
                    break;
                case previous:
                    runSimCommand(new PreviousSimCommand(printWriter));
                    break;
                case setvol:
                    VolumeSimCommand volumeSimCommand = new VolumeSimCommand(printWriter, stringTokenizer);
                    volumeSimCommand.run();
                    break;
                case ping:
                    printWriter.println(Response.OK.toString());
                    break;
                case currentsong:
                    runSimCommand(new CurrentSongSimCommand(printWriter));
                    break;
                default:
                    printWriter.write(Response.ACK + "[5@0] \"" + commandString + "\" not implemented");
                    break;
            }
            System.out.println(getName() + command + " DONE");
        }

        private void runSimCommand(SimCommand simCommand) throws IOException
        {
            try
            {
                simCommand.run();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                printWriter.write(Response.ACK + "[5@0] Unhandled Exception");
            }
        }

        private boolean connected()
        {
            return provider.isConnected();
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
        final BufferedWriter bufferedWriter = mpdSocket.getBufferedWriter();
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
                }
                catch (IOException e)
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
                        bufferedWriter.write(line);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }
                catch (IOException e)
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

        while (true)
        {
            final Socket clientSocket = serverSocket.accept();
            System.out.println("client connected" + clientSocket.getInetAddress());
            final ServerThread serverThread = new ServerThread(new SocketStreamProviderIF()
            {
                public void connect(SocketAddress socketAddress) throws IOException
                {
                }

                public BufferedReader getBufferedReader() throws IOException
                {
                    return new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                }

                public BufferedWriter getBufferedWriter() throws IOException
                {
                    return new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                }

                public void disconnect() throws IOException
                {
                    clientSocket.close();
                }

                public boolean isConnected()
                {
                    return clientSocket.isConnected() && !clientSocket.isClosed();
                }
            });
            serverThread.start();
        }
    }

}
