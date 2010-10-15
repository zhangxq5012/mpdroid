package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.MpdServer;
import com.bender.mpdlib.PlayStatus;

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

    void setMpdServer(MpdServer mpdServer)
    {
        this.mpdServer = mpdServer;
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
                return MpdLibPlayStatus.convertPlayStatus(mpdServer.getPlayStatus());
            }

            public void next()
            {
                mpdServer.next();
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
                mpdServer.stop();
            }

            public MpdSongAdapterIF getCurrentSong()
            {
                return new NullSongAdapter();
            }

            public void addSongChangeListener(MpdSongListener listener)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return new NullPlaylistAdapter();
    }

    private static enum MpdLibPlayStatus
    {
        Playing(PlayStatus.Playing, MpdPlayerAdapterIF.PlayStatus.Playing),
        Paused(PlayStatus.Paused, MpdPlayerAdapterIF.PlayStatus.Paused),
        Stopped(PlayStatus.Stopped, MpdPlayerAdapterIF.PlayStatus.Stopped);

        public final PlayStatus mpdLibStatus;
        public final MpdPlayerAdapterIF.PlayStatus adapterStatus;

        private MpdLibPlayStatus(PlayStatus playing, MpdPlayerAdapterIF.PlayStatus playing1)
        {
            mpdLibStatus = playing;
            adapterStatus = playing1;
        }

        static MpdPlayerAdapterIF.PlayStatus convertPlayStatus(PlayStatus mpdLibPlayStatus)
        {
            for (MpdLibPlayStatus libPlayStatus : values())
            {
                if (libPlayStatus.mpdLibStatus.equals(mpdLibPlayStatus))
                {
                    return libPlayStatus.adapterStatus;
                }
            }
            return null;
        }
    }
}
