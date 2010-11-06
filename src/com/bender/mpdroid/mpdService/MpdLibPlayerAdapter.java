package com.bender.mpdroid.mpdService;

import android.util.Log;
import com.bender.mpdlib.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 */
class MpdLibPlayerAdapter implements MpdPlayerAdapterIF
{
    public MpdPlayStatusListener playStatusListener = new NullMpdPlayStatusListener();
    private Player player;
    private AtomicInteger setVolumeCount = new AtomicInteger(0);
    private static final String TAG = MpdLibPlayerAdapter.class.getSimpleName();

    public MpdLibPlayerAdapter(Player player)
    {
        this.player = player;
    }

    public PlayStatus getPlayStatus()
    {
        return MpdLibPlayStatus.convertPlayStatus(player.getPlayStatus());
    }

    public void next()
    {
        player.next();
    }

    public void prev()
    {
        player.previous();
    }

    public void setVolume(Integer volume)
    {
        setVolumeCount.incrementAndGet();
        player.setVolume(volume);
    }

    public Integer getVolume()
    {
        return player.getVolume();
    }

    public Boolean toggleMute()
    {
        return player.toggleMute();
    }

    public void playOrPause()
    {
        switch (getPlayStatus())
        {
            case Playing:
                player.pause();
                break;
            case Paused:
            case Stopped:
                player.play();
                break;
        }
    }

    public void stop()
    {
        player.stop();
    }

    public MpdSongAdapterIF getCurrentSong()
    {
        return new MpdLibSongAdapter(player.getCurrentSongInfo());
    }

    public void addSongChangeListener(MpdSongListener listener)
    {
        player.addCurrentSongListener(new SongChangeWrapper(listener));
    }

    public void addPlayStatusListener(MpdPlayStatusListener listener)
    {
        playStatusListener = new PlayStatusWrapper(listener);
        player.addPlayStatusListener((PlayStatusListener) playStatusListener);
    }

    public void addVolumeListener(final MpdVolumeListener listener)
    {
        player.addVolumeListener(new VolumeListener()
        {
            public void volumeChanged(Integer volume)
            {
                int count = setVolumeCount.decrementAndGet();
                if (count < 0)
                {
                    setVolumeCount.incrementAndGet();
                    listener.volumeUpdated(volume);
                }
            }
        });
    }

    public void addSongProgressListener(final MpSongProgressListener listener)
    {
        player.addSongProgressListener(new SongProgressListener()
        {
            public void songProgressUpdated(SongProgress songProgress)
            {
                listener.songProgressUpdate(new MpdSongProgressWrapper(songProgress));
            }
        });
    }

    public MpdSongProgress getSongProgress()
    {
        final SongProgress progress = player.getProgress();
        return new MpdSongProgressWrapper(progress);
    }

    private static class MpdSongProgressWrapper implements MpdSongProgress
    {
        private final SongProgress progress;

        public MpdSongProgressWrapper(SongProgress progress)
        {
            this.progress = progress;
        }

        public Integer getCurrentTime()
        {
            return progress.getCurrentTime();
        }

        public Integer getTotalTime()
        {
            return progress.getTotalTime();
        }
    }

    class PlayStatusWrapper implements MpdPlayStatusListener, PlayStatusListener
    {
        private MpdPlayStatusListener theListener;

        public PlayStatusWrapper(MpdPlayStatusListener listener)
        {
            theListener = listener;
        }

        public void playStatusUpdated(PlayStatus playStatus)
        {
            theListener.playStatusUpdated(playStatus);
        }

        public void playStatusChanged(com.bender.mpdlib.PlayStatus playStatus)
        {
            playStatusUpdated(MpdLibPlayStatus.convertPlayStatus(playStatus));
        }
    }

    class NullMpdPlayStatusListener implements MpdPlayStatusListener
    {
        public void playStatusUpdated(PlayStatus playStatus)
        {
            Log.v(TAG, "playStatusUpdated() on NULL object");
        }
    }

    private class MpdLibSongAdapter implements MpdSongAdapterIF
    {

        private SongInfo songInfo;

        public MpdLibSongAdapter(SongInfo songInfo)
        {
            this.songInfo = songInfo;
        }

        public String getSongName()
        {
            return songInfo.getValue(SongInfo.SongAttributeType.Title);
        }

        public String getArtist()
        {
            return songInfo.getValue(SongInfo.SongAttributeType.Artist);
        }

        public String getAlbumName()
        {
            return songInfo.getValue(SongInfo.SongAttributeType.Album);
        }

        public String getFile()
        {
            return songInfo.getValue(SongInfo.SongAttributeType.file);
        }

        public String getDate()
        {
            return songInfo.getValue(SongInfo.SongAttributeType.Date);
        }

        public Integer getSongLength()
        {
            try
            {
                return Integer.valueOf(songInfo.getValue(SongInfo.SongAttributeType.Time));
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }
    }

    private class SongChangeWrapper implements CurrentSongListener
    {
        private MpdSongListener listener;

        public SongChangeWrapper(MpdSongListener listener)
        {
            this.listener = listener;
        }

        public void songUpdated(SongInfo songInfo)
        {
            listener.songUpdated(new MpdLibSongAdapter(songInfo));
        }
    }

    private static enum MpdLibPlayStatus
    {
        Playing(com.bender.mpdlib.PlayStatus.Playing, MpdPlayerAdapterIF.PlayStatus.Playing),
        Paused(com.bender.mpdlib.PlayStatus.Paused, MpdPlayerAdapterIF.PlayStatus.Paused),
        Stopped(com.bender.mpdlib.PlayStatus.Stopped, MpdPlayerAdapterIF.PlayStatus.Stopped);

        public final com.bender.mpdlib.PlayStatus mpdLibStatus;
        public final MpdPlayerAdapterIF.PlayStatus adapterStatus;

        private MpdLibPlayStatus(com.bender.mpdlib.PlayStatus playing, MpdPlayerAdapterIF.PlayStatus playing1)
        {
            mpdLibStatus = playing;
            adapterStatus = playing1;
        }

        static MpdPlayerAdapterIF.PlayStatus convertPlayStatus(com.bender.mpdlib.PlayStatus mpdLibPlayStatus)
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
