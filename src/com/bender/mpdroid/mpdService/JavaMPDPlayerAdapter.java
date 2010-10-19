package com.bender.mpdroid.mpdService;

import android.util.Log;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.events.PlayerChangeEvent;
import org.bff.javampd.events.PlayerChangeListener;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.objects.MPDSong;

/**
 * JavaMPD implementation of #MpdPlayerAdapterIF
 */
class JavaMPDPlayerAdapter implements MpdPlayerAdapterIF
{
    private static final String TAG = JavaMPDPlayerAdapter.class.getSimpleName();

    private MPDPlayer mpdPlayer;
    private boolean muted;
    private JavaMPDPlayerChangeListener playerChangeListener;
    private MPDSong currentSong;
    private CurrentSongRunnable currentSongThread;
    private MpdSongListener myListener;

    public JavaMPDPlayerAdapter(MPDPlayer mpdPlayer)
    {
        this.mpdPlayer = mpdPlayer;
        playerChangeListener = new JavaMPDPlayerChangeListener();
        this.mpdPlayer.addPlayerChangeListener(playerChangeListener);
        currentSongThread = new CurrentSongRunnable();
        currentSongThread.start();
        myListener = new NullMpdSongListener();
    }

    public PlayStatus getPlayStatus()
    {
        PlayStatus status = PlayStatus.Stopped;
        try
        {
            status = JavaMDPPlayStatus.convertFromJavaMDPStatus(mpdPlayer.getStatus());
        }
        catch (MPDException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return status;
    }

    public void next()
    {
        try
        {
            mpdPlayer.playNext();
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    public void prev()
    {
        try
        {
            mpdPlayer.playPrev();
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    public Integer setVolume(Integer volume)
    {
        Integer currentVolume = 0;
        try
        {
            mpdPlayer.setVolume(volume);
            currentVolume = mpdPlayer.getVolume();
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return currentVolume;
    }

    public Integer getVolume()
    {
        Integer volume = 0;
        try
        {
            volume = mpdPlayer.getVolume();
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return volume;
    }

    public Boolean toggleMute()
    {
        try
        {
            if (muted)
            {
                mpdPlayer.unMute();
            }
            else
            {
                mpdPlayer.mute();
            }
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
        }
        return muted;
    }

    public void playOrPause()
    {
        try
        {
            MPDPlayer.PlayerStatus status = mpdPlayer.getStatus();
            if (status.equals(MPDPlayer.PlayerStatus.STATUS_PLAYING))
            {
                mpdPlayer.pause();
            }
            else
            {
                mpdPlayer.play();
            }
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    public void stop()
    {
        try
        {
            mpdPlayer.stop();
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
    }

    public MpdSongAdapterIF getCurrentSong()
    {
        MpdSongAdapterIF songAdapterIF = new NullSongAdapter();
        try
        {
            if (currentSong != null)
            {
                songAdapterIF = new JavaMPDSongAdapter(mpdPlayer.getCurrentSong());
            }
        }
        catch (MPDConnectionException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        catch (MPDPlayerException e)
        {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return songAdapterIF;
    }

    public void addSongChangeListener(MpdSongListener listener)
    {
        myListener = listener;
    }

    public void addPlayStatusListener(MpdPlayStatusListener listener)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addVolumeListener(MpdVolumeListener listener)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    void cleanUp()
    {
        mpdPlayer.removePlayerChangedListener(playerChangeListener);
        currentSongThread.disconnected = true;
        currentSongThread.interrupt();
        mpdPlayer = null;
        currentSongThread = null;
        currentSong = null;
    }

    private static enum JavaMDPPlayStatus
    {
        Playing(MPDPlayer.PlayerStatus.STATUS_PLAYING, PlayStatus.Playing),
        Paused(MPDPlayer.PlayerStatus.STATUS_PAUSED, PlayStatus.Paused),
        Stopped(MPDPlayer.PlayerStatus.STATUS_STOPPED, PlayStatus.Stopped);

        private final MPDPlayer.PlayerStatus javaMPDStatus;
        private final PlayStatus playStatus;

        private JavaMDPPlayStatus(MPDPlayer.PlayerStatus statusPlaying, PlayStatus playing)
        {
            javaMPDStatus = statusPlaying;
            playStatus = playing;
        }

        private static PlayStatus convertFromJavaMDPStatus(MPDPlayer.PlayerStatus status)
        {
            PlayStatus ret = PlayStatus.Stopped;
            for (JavaMDPPlayStatus javaMDPPlayStatus : values())
            {
                if (javaMDPPlayStatus.javaMPDStatus.equals(status))
                {
                    ret = javaMDPPlayStatus.playStatus;
                }
            }
            return ret;
        }
    }

    private class JavaMPDPlayerChangeListener implements PlayerChangeListener
    {
        public void playerChanged(PlayerChangeEvent event)
        {
            switch (event.getId())
            {
                case PlayerChangeEvent.PLAYER_MUTED:
                    muted = true;
                    break;
                case PlayerChangeEvent.PLAYER_UNMUTED:
                    muted = false;
                    break;
            }
        }
    }

    private class CurrentSongRunnable extends Thread
    {
        private volatile boolean disconnected;

        public void run()
        {
            while (!disconnected)
            {
                try
                {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e)
                {
                    Log.i(TAG, "interrupted");
                }
                if (disconnected)
                {
                    return;
                }
                try
                {
                    currentSong = mpdPlayer.getCurrentSong();
                    myListener.songUpdated(new JavaMPDSongAdapter(currentSong));
                }
                catch (MPDConnectionException e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "", e);
                }
                catch (MPDPlayerException e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "", e);
                }
            }
        }

    }

    private class NullMpdSongListener implements MpdSongListener
    {
        public void songUpdated(MpdSongAdapterIF song)
        {
            //no-op
        }
    }
}
