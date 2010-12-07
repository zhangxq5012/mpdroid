package com.bender.mpdroid.mpdService;

import com.bender.mpdlib.ConnectionListener;
import com.bender.mpdlib.MpdServer;

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
        return new NullPlaylistAdapter();
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


}
