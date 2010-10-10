package com.bender.mpdroid;

/**
 * An adapter interface to control the mpd server.
 */
public interface MpdAdapterIF
{
    PlayStatus getPlayStatus();

    void next();

    void prev();

    Integer setVolume(Integer volume);

    Integer getVolume();

    Boolean toggleMute();

    public enum PlayStatus
    {
        Playing,
        Paused,
        Stopped
    }

    void connect(String server, int port, String password);

    void connect(String server, int port);

    void connect(String server);

    void disconnect();

    boolean isConnected();

    String getServerVersion();

    PlayStatus playOrPause();

}
