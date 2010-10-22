package com.bender.mpdlib;

import com.bender.mpdlib.commands.MpdCommands;
import com.bender.mpdlib.commands.Response;
import junit.framework.TestCase;

/**
 * todo: replace with documentation
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    public static final String HOSTNAME = "hostname";
    private static final int PORT = 6601;
    public static final String VERSION = "MPD 0.15.0";
    private MockCommandStreamProvider commandStreamProvider;
    private MockCallbackStreamProvider callbackStreamProvider;

    @Override
    public void setUp() throws Exception
    {
        commandStreamProvider = new MockCommandStreamProvider();
        callbackStreamProvider = new MockCallbackStreamProvider();
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
        Player player = mpdServer.getPlayer();
        player.play();

        assertLastCommandEquals(MpdCommands.play.toString());
    }


    public void testDisconnect() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        mpdServer.disconnect();

        assertLastCommandEquals(MpdCommands.close.toString());
    }

    public void testStatus() throws Exception
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.state).append(": ");
        stringBuilder.append(PlayStatus.Paused.serverString);
        commandStreamProvider.removeLastCommand();
        setStatus(stringBuilder);

        mpdServer.connect(HOSTNAME);

        Player player = mpdServer.getPlayer();
        PlayStatus playStatus = player.getPlayStatus();

        assertEquals(PlayStatus.Paused, playStatus);
    }

    public void testStop() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        Player player = mpdServer.getPlayer();
        player.stop();

        assertLastCommandEquals(MpdCommands.stop.toString());
    }

    public void testNext() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        Player player = mpdServer.getPlayer();

        player.next();

        assertLastCommandEquals(MpdCommands.next.toString());
    }


    public void testPrev() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        commandStreamProvider.appendServerResult(Response.OK.toString());
        Player player = mpdServer.getPlayer();
        player.previous();

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

    public void testSongName() throws Exception
    {
        Integer songId = 1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MpdStatus.songid).append(": ");
        stringBuilder.append(songId);
        commandStreamProvider.removeLastCommand();
        setStatus(stringBuilder);

        final String name = "Song title";
        commandStreamProvider.appendServerResult(SongInfo.SongAttributeType.Title + ": " + name);
        commandStreamProvider.appendServerResult(Response.OK);
        mpdServer.connect(HOSTNAME);

        SongInfo songInfo = mpdServer.getPlayer().getCurrentSongInfo();
        assertLastCommandEquals(MpdCommands.currentsong.toString());
        assertNotNull(songInfo);
        assertEquals(name, songInfo.getValue(SongInfo.SongAttributeType.Title));
    }

    public void testSongListener() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        MyCurrentSongListener currentSongListener = new MyCurrentSongListener();
        mpdServer.getPlayer().addCurrentSongListener(currentSongListener);

        final Integer songId = 1;
        final String songTitle = "Test Song Title";
        // idle
        callbackStreamProvider.appendResponse("changed: " + Subsystem.player);
        callbackStreamProvider.appendResponse(Response.OK);
        //status
        callbackStreamProvider.appendResponse(MpdStatus.songid + ": " + songId);
        callbackStreamProvider.appendResponse(Response.OK);
        // currentsong
        commandStreamProvider.appendServerResult(SongInfo.SongAttributeType.Id + ": " + songId);
        commandStreamProvider.appendServerResult(SongInfo.SongAttributeType.Title + ": " + songTitle);
        commandStreamProvider.appendServerResult(Response.OK);

        synchronized (this)
        {
            wait(100);
        }
        callbackStreamProvider.changeEvent();
        synchronized (this)
        {
            wait(100);
        }
        assertEquals(true, currentSongListener.songUpdated);
        assertEquals(songId.toString(), currentSongListener.currentSong.getValue(SongInfo.SongAttributeType.Id));
        assertEquals(songTitle, currentSongListener.currentSong.getValue(SongInfo.SongAttributeType.Title));
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

        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        player.addPlayStatusListener(listener);

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
        commandStreamProvider.assertLastCommandEquals(command);
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

    private class MyCurrentSongListener implements CurrentSongListener
    {
        private boolean songUpdated;
        private SongInfo currentSong;

        public void songUpdated(SongInfo songInfo)
        {
            songUpdated = true;
            currentSong = songInfo;
        }
    }

    private class MyPlayStatusListener implements PlayStatusListener
    {
        private boolean playStatusUpdated;
        private PlayStatus playStatus;

        public void playStatusChanged()
        {
            playStatusUpdated = true;
            playStatus = mpdServer.getPlayer().getPlayStatus();
        }
    }
}
