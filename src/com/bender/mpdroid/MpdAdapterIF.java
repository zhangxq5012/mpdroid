package com.bender.mpdroid;

/**
 * todo: replace with documentation
 */
public interface MpdAdapterIF
{
    PlayStatus getPlayStatus();

    public enum PlayStatus
    {
        Playing,
        Paused,
        Stopped
    }

    void connect(String server, int port, String password);

    void connect(String server);

    void disconnect();

    boolean isConnected();

    String getServerVersion();

    PlayStatus playOrPause();

}
