package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.MpdServer;

/**
 * todo: replace with documentation
 */
public class MpdLibServiceAdapter implements MpdServiceAdapterIF
{
    private MpdServer mpdServer;

    public MpdLibServiceAdapter()
    {
        mpdServer = new MpdServer();
    }

    public void connect(String server, int port, String password)
    {
        mpdServer.connect(server, port, password);
    }

    public void connect(String server, int port)
    {
        mpdServer.connect(server, port);
    }

    public void connect(String server)
    {
        mpdServer.connect(server);
    }

    public void disconnect()
    {
        mpdServer.disconnect();
    }

    public boolean isConnected()
    {
        return mpdServer.isConnected();
    }

    public String getServerVersion()
    {
        return mpdServer.getServerVersion();
    }

    public MpdPlayerAdapterIF getPlayer()
    {
        return new MpdPlayerAdapterIF()
        {
            public PlayStatus getPlayStatus()
            {
                return PlayStatus.Stopped;
            }

            public void next()
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public void prev()
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public Integer setVolume(Integer volume)
            {
                return 0;
            }

            public Integer getVolume()
            {
                return 0;
            }

            public Boolean toggleMute()
            {
                return true;
            }

            public void playOrPause()
            {
                mpdServer.play();
            }

            public void stop()
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            public MpdSongAdapterIF getCurrentSong()
            {
                return new NullSongAdapter();
            }

            public void addPlayerListener(MpdSongListener listener)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return new NullPlaylistAdapter();
    }
}
