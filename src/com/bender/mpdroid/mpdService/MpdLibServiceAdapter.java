package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.ConnectionListener;
import com.bender.mpdlib.MpdServer;
import com.bender.mpdlib.Options;

/**
 */
public class MpdLibServiceAdapter implements MpdServiceAdapterIF
{
    private MpdServer mpdServer;
//    private static final String TAG = MpdLibServiceAdapter.class.getSimpleName();

    public MpdLibServiceAdapter()
    {
        mpdServer = new MpdServer();
        com.bender.mpdlib.util.Log.setLogStrategy(new AndroidLogStrategy());
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
        return new MpdLibPlayerAdapter(mpdServer.getPlayer());
    }

    public MpdPlaylistAdapterIF getPlaylist()
    {
        return new MpdLibPlaylistAdapter(mpdServer.getPlaylist());
    }

    public MpdOptionsIF getOptions()
    {
        return new MpdOptionsImpl(mpdServer.getOptions());
    }

    public void addConnectionListener(final MpdConnectionListenerIF connectionListenerIF)
    {
        mpdServer.addConnectionListener(new ConnectionListener()
        {
            public void connectionChanged(boolean isConnected)
            {
                connectionListenerIF.connectionStateUpdated(isConnected);
            }
        });
    }


    private static class MpdOptionsImpl implements MpdOptionsIF
    {
        private Options options;

        public MpdOptionsImpl(Options options)
        {
            this.options = options;
        }

        public void toggleRepeat()
        {
            options.toggleRepeat();
        }

        public Boolean getRepeat()
        {
            return options.getRepeat();
        }

        public void addOptionsListener(final OptionsListener optionsListener)
        {
            options.setListener(new com.bender.mpdlib.OptionsListener()
            {
                public void repeatUpdated(boolean repeat)
                {
                    optionsListener.repeatUpdated(repeat);
                }

                public void randomUpdated(boolean newRandom)
                {
                    optionsListener.randomUpdated(newRandom);
                }
            });
        }

        public void toggleRandom()
        {
            options.toggleRandom();
        }

        public Boolean getRandom()
        {
            return options.getRandom();
        }
    }
}
