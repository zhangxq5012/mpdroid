package com.bender.mpdlib;

/**
 * todo: replace with documentation
 */
public class MpdPlayer
{
    private Pipe commandPipe;
    private MpdServer mpdServer;
    private static final String TAG = MpdPlayer.class.getSimpleName();

    public MpdPlayer(MpdServer mpdServer, Pipe commandPipe)
    {
        this.mpdServer = mpdServer;
        this.commandPipe = commandPipe;
    }

//    public PlayStatus getPlayStatus()
//    {
//        return PlayStatus.Stopped;
//    }

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
        return false;
    }

    public void playOrPause()
    {
    }


    public void stop()
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

//    public MpdSongAdapterIF getCurrentSong()
//    {
//        return new NullSongAdapter();
//    }

//    public void addPlayerListener(MpdSongListener listener)
//    {
    //To change body of implemented methods use File | Settings | File Templates.
//    }
}
