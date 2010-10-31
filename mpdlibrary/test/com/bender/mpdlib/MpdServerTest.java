package com.bender.mpdlib;

import com.bender.mpdlib.simulator.MpdServerSimulator;
import com.bender.mpdlib.simulator.commands.SubSystemSupport;
import junit.framework.TestCase;

/**
 */
public class MpdServerTest extends TestCase
{
    private MpdServer mpdServer;
    public static final String HOSTNAME = "localhost";
    private static final int PORT = 6601;
    public static final String VERSION = MpdServerSimulator.VERSION;
    private MpdServerSimulator mpdServerSimulator;

    @Override
    public void setUp() throws Exception
    {
        mpdServerSimulator = new MpdServerSimulator();
        mpdServerSimulator.getSubSystemSupport().setIdleStrategy(new SingleThreadIdleStrategy());
        SocketStreamProviderIF mpdSocket = mpdServerSimulator.createMpdSocket();
        SocketStreamProviderIF mpdSocket1 = mpdServerSimulator.createMpdSocket();
        mpdServer = new MpdServer(mpdSocket, mpdSocket1);
    }

    @Override
    public void tearDown() throws Exception
    {
        mpdServer.disconnect();
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

        Player player = mpdServer.getPlayer();
        //wait for idle
        smallWait();
        player.play();
        // wait for callback
        smallWait();
        PlayStatus playStatus = player.getPlayStatus();

        assertEquals(PlayStatus.Playing, mpdServerSimulator.getSimPlayer().getCurrentPlayStatus());
        assertEquals(PlayStatus.Playing, playStatus);
    }


//    public void testDisconnect() throws Exception
//    {
//        mpdServer.connect(HOSTNAME);

    //todo: how to test?
//        commandStreamProvider.appendServerResult(Response.OK.toString());
//        mpdServer.disconnect();
//
//        assertEquals(false,mpdServer.isConnected());
//    }

    public void testStatus() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        Player player = mpdServer.getPlayer();
        smallWait();
        player.play();
        smallWait();
        player.pause();
        smallWait();
        PlayStatus playStatus = player.getPlayStatus();

        assertEquals(PlayStatus.Paused, playStatus);
    }

    private void smallWait()
            throws InterruptedException
    {
        synchronized (this)
        {
            wait(100);
        }
    }

    public void testStop() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        smallWait();

        Player player = mpdServer.getPlayer();
        player.play();
        smallWait();
        player.stop();
        smallWait();

        PlayStatus playStatus = player.getPlayStatus();

        assertEquals(PlayStatus.Stopped, playStatus);
    }

    public void testNext() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        smallWait();

        Player player = mpdServer.getPlayer();
        player.next();
        smallWait();

        int simId = Integer.valueOf(mpdServerSimulator.getPlaylist().getCurrentSong().getValue(SongInfo.SongAttributeType.Id));
        int id = Integer.valueOf(player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.Id));
        assertEquals(2, simId);
        assertEquals(2, id);
    }


    public void testPrev() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        smallWait();

        Player player = mpdServer.getPlayer();
        player.previous();
        smallWait();


        int simId = Integer.valueOf(mpdServerSimulator.getPlaylist().getCurrentSong().getValue(SongInfo.SongAttributeType.Id));
        int id = Integer.valueOf(player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.Id));
        int size = mpdServerSimulator.getPlaylist().size();
        assertEquals(size, simId);
        assertEquals(size, id);
    }

    public void testGetVolume() throws Exception
    {
        final Integer VOLUME = 75;

        mpdServerSimulator.getSimPlayer().setVolume(VOLUME);
        mpdServer.connect(HOSTNAME);

        Integer volume = mpdServer.getPlayer().getVolume();

        assertEquals(VOLUME, volume);
    }

    public void testSongName() throws Exception
    {
        final String name = "Song title";
        mpdServerSimulator.getPlaylist().getCurrentSong().updateValue(SongInfo.SongAttributeType.Title, name);
        mpdServer.connect(HOSTNAME);

        SongInfo songInfo = mpdServer.getPlayer().getCurrentSongInfo();
        assertNotNull(songInfo);
        assertEquals(name, songInfo.getValue(SongInfo.SongAttributeType.Title));
    }

    public void testSongListener() throws Exception
    {

        mpdServer.connect(HOSTNAME);
        smallWait();

        final Integer songId = 0;
        final String songTitle = "Test Song Title";
        SongInfo currentSong = mpdServerSimulator.getPlaylist().getCurrentSong();
        currentSong.updateValue(SongInfo.SongAttributeType.Title, songTitle);
        currentSong.updateValue(SongInfo.SongAttributeType.Id, songId.toString());

        MyCurrentSongListener currentSongListener = new MyCurrentSongListener();
        mpdServer.getPlayer().addCurrentSongListener(currentSongListener);
        smallWait();

        mpdServerSimulator.getSubSystemSupport().updateSubSystemChanged(Subsystem.player);
        smallWait();

        assertEquals(true, currentSongListener.songUpdated);
        assertEquals(songTitle, currentSongListener.currentSong.getValue(SongInfo.SongAttributeType.Title));
    }

    public void testSetVolume() throws Exception
    {
        mpdServer.connect(HOSTNAME);
        smallWait();

        Integer volume = 75;

        mpdServer.getPlayer().setVolume(volume);
        smallWait();

        assertEquals(volume, mpdServerSimulator.getSimPlayer().getVolume());
        assertEquals(volume, mpdServer.getPlayer().getVolume());
    }


    public void testVolumeListener() throws Exception
    {

        final Integer volume = 75;
        mpdServer.connect(HOSTNAME);
        smallWait();

        mpdServerSimulator.getSimPlayer().setVolume(volume);

        MyVolumeListener listener = new MyVolumeListener();
        mpdServer.getPlayer().addVolumeListener(listener);
        smallWait();

        mpdServerSimulator.getSubSystemSupport().updateSubSystemChanged(Subsystem.mixer);
        smallWait();

        assertEquals(true, listener.volumeChanged);
        assertEquals(volume, listener.newVolume);
    }

    public void testPlayListener() throws Exception
    {
        MyPlayStatusListener listener = new MyPlayStatusListener();

        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        player.addPlayStatusListener(listener);

        smallWait();
        final PlayStatus playStatus = PlayStatus.Playing;
        mpdServerSimulator.getSimPlayer().updatePlayStatus(playStatus);

        mpdServerSimulator.getSubSystemSupport().updateSubSystemChanged(Subsystem.player);
        smallWait();

        assertEquals(true, listener.playStatusUpdated);
        assertEquals(playStatus, listener.playStatus);
    }

    public void testArtist() throws Exception
    {
        final String ARTIST = "Test Artist";
        mpdServerSimulator.getPlaylist().getCurrentSong().updateValue(SongInfo.SongAttributeType.Artist, ARTIST);
        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        smallWait();

        String artist = player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.Artist);

        assertEquals(ARTIST, artist);
    }

    public void testAlbum() throws Exception
    {
        final String ALBUM = "Test Album";
        mpdServerSimulator.getPlaylist().getCurrentSong().updateValue(SongInfo.SongAttributeType.Album, ALBUM);

        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        smallWait();

        String album = player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.Album);
        assertEquals(ALBUM, album);
    }

    public void testFilename() throws Exception
    {
        final String FILENAME = "/test/path/filename.mp3";
        mpdServerSimulator.getPlaylist().getCurrentSong().updateValue(SongInfo.SongAttributeType.file, FILENAME);

        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        smallWait();

        String filename = player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.file);
        assertEquals(FILENAME, filename);
    }

    public void testDate() throws Exception
    {
        final String DATE = "2010";
        mpdServerSimulator.getPlaylist().getCurrentSong().updateValue(SongInfo.SongAttributeType.Date, DATE);

        mpdServer.connect(HOSTNAME);
        Player player = mpdServer.getPlayer();
        smallWait();

        String date = player.getCurrentSongInfo().getValue(SongInfo.SongAttributeType.Date);
        assertEquals(DATE, date);
    }

    public void testMute() throws Exception
    {
        mpdServer.connect(HOSTNAME);

        smallWait();
        Player player = mpdServer.getPlayer();
        player.toggleMute();
        smallWait();

        assertEquals(true, player.isMuted());
        Integer zero = 0;
        assertEquals(zero, player.getVolume());

        player.toggleMute();
        smallWait();

        assertEquals(false, player.isMuted());
        Integer hundred = 100;
        assertEquals(hundred, player.getVolume());

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

    private static class SingleThreadIdleStrategy implements SubSystemSupport.IdleStrategy
    {
        public void execute(Runnable runnable)
        {
            runnable.run();
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

        public void playStatusChanged(PlayStatus playStatus)
        {
            playStatusUpdated = true;
            this.playStatus = playStatus;
        }
    }
}
