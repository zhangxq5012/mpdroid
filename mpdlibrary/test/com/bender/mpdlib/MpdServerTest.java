package com.bender.mpdlib;

import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import junit.framework.TestCase;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * todo: replace with documentation
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    public static final String HOSTNAME = "hostname";
    private static final int PORT = 6601;
    public static final String VERSION = "MPD 0.15.0";
    private CommandStreamProvider commandStreamProvider;
    private CallbackStreamProvider callbackStreamProvider;

    @Override
    public void setUp() throws Exception
    {
        commandStreamProvider = new CommandStreamProvider();
        callbackStreamProvider = new CallbackStreamProvider();
        mpdServer = new MpdServer(commandStreamProvider, callbackStreamProvider);
        commandStreamProvider.appendServerResult(Response.OK + " " + VERSION);
        callbackStreamProvider.appendResponse(Response.OK + " " + VERSION);
        StringBuilder stringBuilder = new StringBuilder();
        setStatus(stringBuilder);
    }

    public void testConnectWithHostname() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithHostnameAndPort() throws Exception
    {
        mpdServer.connect(HOSTNAME, PORT);
        assertEquals(true, mpdServer.isConnected());
    }

    public void testConnectWithAuthenticationUnsupported() throws Exception
    {
        boolean unsupported = false;
        try
        {
            mpdServer.connect(HOSTNAME, PORT, "password");
        }
        catch (IllegalArgumentException e)
        {
            unsupported = true;
        }
        assertEquals(true, unsupported);
    }

    public void testVersion() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        String version = mpdServer.getServerVersion();
        assertEquals(VERSION, version);
    }

    public void testPlay() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.play();

        List<String> commandQueue = (List<String>) commandStreamProvider.commandQueue;
        assertEquals(true, commandQueue.contains(MpdCommands.play.toString()));
    }


    public void testDisconnect() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.disconnect();

        List<String> commandQueue = (List<String>) commandStreamProvider.commandQueue;
        assertEquals(MpdCommands.close.toString(), commandQueue.get(commandQueue.size() - 1));
    }

    public void testStatus() throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.state).append(": ");
        stringBuilder.append(PlayStatus.Paused.serverString);
        commandStreamProvider.removeLastCommand();
        setStatus(stringBuilder);

        mpdServer.connect(HOSTNAME);

        PlayStatus playStatus = mpdServer.getPlayStatus();

        assertEquals(PlayStatus.Paused, playStatus);
    }

    public void testStop() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.stop();

        assertLastCommandEquals(MpdCommands.stop.toString());
    }

    public void testNext() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());

        mpdServer.next();

        assertLastCommandEquals(MpdCommands.next.toString());
    }


    public void testPrev() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.previous();

        assertLastCommandEquals(MpdCommands.previous.toString());
    }

    public void testGetVolume() throws Exception
    {
        final Integer VOLUME = 75;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.volume).append(": ");
        stringBuilder.append(VOLUME);
        commandStreamProvider.removeLastCommand();
        setStatus(stringBuilder);

        mpdServer.connect(HOSTNAME);

        Integer volume = mpdServer.getVolume();

        assertEquals(VOLUME, volume);
    }

    public void testSetVolume() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        Integer volume = 75;

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.setVolume(volume);

        assertLastCommandEquals("setvol " + volume);
    }

    public void testVolumeListener() throws Exception
    {
        MyVolumeListener listener = new MyVolumeListener();
        mpdServer.addVolumeListener(listener);

        final Integer volume = 75;
        mpdServer.connect(HOSTNAME);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.volume).append(": ");
        stringBuilder.append(volume);
        callbackStreamProvider.appendResponse("changed: " + Subsystem.mixer);
        callbackStreamProvider.appendResponse(Response.OK.toString());
        callbackStreamProvider.appendResponse(stringBuilder.toString());
        callbackStreamProvider.appendResponse(Response.OK.toString());

        synchronized (this)
        {
            wait(100L);
        }
        callbackStreamProvider.changeEvent();
        synchronized (this)
        {
            wait(100L);
        }

        assertEquals(true, listener.volumeChanged);
        assertEquals(volume, listener.newVolume);
    }

    public void testPlayListener() throws Exception
    {
        MyPlayStatusListener listener = new MyPlayStatusListener();
        mpdServer.addPlayStatusListener(listener);

        mpdServer.connect(HOSTNAME);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.state).append(": ");
        stringBuilder.append(PlayStatus.Playing.serverString);
        callbackStreamProvider.appendResponse("changed: " + Subsystem.player);
        callbackStreamProvider.appendResponse(Response.OK.toString());
        callbackStreamProvider.appendResponse(stringBuilder.toString());
        callbackStreamProvider.appendResponse(Response.OK.toString());

        synchronized (this)
        {
            wait(100L);
        }
        callbackStreamProvider.changeEvent();
        synchronized (this)
        {
            wait(100L);
        }

        assertEquals(true, listener.playStatusUpdated);
        assertEquals(PlayStatus.Playing, listener.playStatus);
    }

    private void assertLastCommandEquals(String command)
    {
        List<String> commandQueue = (List<String>) commandStreamProvider.commandQueue;
        assertEquals(command, commandQueue.get(commandQueue.size() - 1));
    }

    private void setStatus(StringBuilder stringBuilder)
    {
        String s = stringBuilder.toString();
        if (!s.equals(""))
        {
            commandStreamProvider.appendServerResult(s);
        }
        commandStreamProvider.appendServerResult(Response.OK.toString());
    }

    private static class MyVolumeListener implements VolumeListener
    {
        private boolean volumeChanged;
        private Integer newVolume;

        public void volumeChanged(Integer volume)
        {
            volumeChanged = true;
            newVolume = volume;
        }
    }

    public class CommandStreamProvider implements SocketStreamProviderIF
    {
        private BufferedWriter bufferedWriter;
        private BufferedReader bufferedReader;
        private Queue<String> commandQueue;
        private Queue<String> responseQueue;

        private boolean connected;

        public CommandStreamProvider() throws IOException
        {
            bufferedReader = mock(BufferedReader.class);
            bufferedWriter = mock(BufferedWriter.class);
            commandQueue = new LinkedList<String>();
            responseQueue = new LinkedList<String>();
            when(bufferedReader.readLine()).thenAnswer(new Answer<String>()
            {
                public String answer(InvocationOnMock invocationOnMock) throws Throwable
                {
                    return responseQueue.remove();
                }
            });
            doAnswer(new Answer()
            {
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable
                {
                    commandQueue.offer((String) invocationOnMock.getArguments()[0]);
                    return null;
                }
            }).when(bufferedWriter).write(anyString());
        }

        public void connect(SocketAddress socketAddress) throws IOException
        {
            connected = true;
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            return bufferedReader;
        }

        public BufferedWriter getBufferedWriter() throws IOException
        {
            return bufferedWriter;
        }

        public void disconnect() throws IOException
        {
            connected = false;
        }

        public boolean isConnected()
        {
            return connected;
        }

        public void appendServerResult(String s)
        {
            responseQueue.offer(s);
        }

        public void removeLastCommand()
        {
            List<String> list = (List<String>) responseQueue;
            list.remove(list.size() - 1);
        }
    }

    private class CallbackStreamProvider implements SocketStreamProviderIF
    {
        private boolean connected;
        private BufferedReader reader;
        private Queue<String> responseQueue = new LinkedList<String>();

        public void connect(SocketAddress socketAddress) throws IOException
        {
            connected = true;
        }

        public BufferedReader getBufferedReader() throws IOException
        {
            reader = mock(BufferedReader.class);
            when(reader.readLine()).thenAnswer(new Answer<String>()
            {
                public String answer(InvocationOnMock invocationOnMock) throws Throwable
                {
                    return responseQueue.remove();
                }
            });
            return reader;
        }

        public BufferedWriter getBufferedWriter() throws IOException
        {
            BufferedWriter mock = mock(BufferedWriter.class);
            doAnswer(new Answer()
            {
                public Object answer(InvocationOnMock invocationOnMock) throws Throwable
                {
                    if (invocationOnMock.getArguments()[0].equals(MpdCommands.idle.toString()))
                    {
                        synchronized (CallbackStreamProvider.this)
                        {
                            CallbackStreamProvider.this.wait();
                        }
                    }
                    return null;
                }
            }).when(mock).write(anyString());
            return mock;
        }

        public void disconnect() throws IOException
        {
            connected = false;
        }

        public boolean isConnected()
        {
            return connected;
        }

        public void changeEvent()
        {
            synchronized (this)
            {
                notifyAll();
            }
        }

        public void appendResponse(String string)
        {
            responseQueue.offer(string);
        }
    }

    private class MyPlayStatusListener implements PlayStatusListener
    {
        private boolean playStatusUpdated;
        private PlayStatus playStatus;

        public void playStatusChanged()
        {
            playStatusUpdated = true;
            playStatus = mpdServer.getPlayStatus();
        }
    }
}
