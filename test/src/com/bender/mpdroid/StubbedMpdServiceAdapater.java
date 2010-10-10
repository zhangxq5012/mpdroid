package com.bender.mpdroid;

import com.bender.mpdroid.mpdService.MpdPlayerAdapterIF;
import com.bender.mpdroid.mpdService.MpdPlaylistAdapterIF;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;
import com.bender.mpdroid.mpdService.MpdSongAdapterIF;

/**
 * This is a stubbed mpd adapter. It is meant to be used for testing.
 */
public class StubbedMpdServiceAdapater implements MpdServiceAdapterIF
{
    private boolean connected;
    public int connectCount;
    public int disconnectCount;

    public void connect(String server, int port, String password)
    {
        connect();
    }

    public void connect(String server, int port)
    {
        connect();
    }

    public void connect(String server)
    {
        connect();
    }

    private void connect()
    {
        connected = true;
        connectCount++;
    }

    public void disconnect()
    {
        connected = false;
        disconnectCount++;
    }

    public boolean isConnected()
    {
        return connected;
    }

    public String getServerVersion()
    {
        return "Stubbed MPD Server";
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        return new StubbedPlayerAdapter();
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return new StubbedPlaylistAdapter();
    }


    private class StubbedPlayerAdapter implements MpdPlayerAdapterIF
    {
        private Integer volume;

        public PlayStatus playOrPause()
        {
            return PlayStatus.Stopped;
        }

        public void stop()
        {
        }

        public PlayStatus getPlayStatus()
        {
            return PlayStatus.Stopped;
        }

        public void next()
        {
        }

        public void prev()
        {
        }

        public Integer setVolume(Integer volume)
        {
            this.volume = volume;
            return getVolume();
        }

        public Integer getVolume()
        {
            return this.volume;
        }

        public Boolean toggleMute()
        {
            return false;
        }
    }

    private class StubbedPlaylistAdapter implements MpdPlaylistAdapterIF
    {
        public MpdSongAdapterIF getCurrentSong()
        {
            return new StubbedSongAdapter();
        }

        private class StubbedSongAdapter implements MpdSongAdapterIF
        {
            public String getSongName()
            {
                return null;
            }
        }
    }
}
