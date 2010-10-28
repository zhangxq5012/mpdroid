package com.bender.mpdroid.mpdService;

import android.util.Log;
import com.bender.mpdlib.*;

/**
 */
public class MpdLibServiceAdapter implements MpdServiceAdapterIF
{
    private MpdServer mpdServer;
    private static final String TAG = MpdLibServiceAdapter.class.getSimpleName();

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
        return new MyMpdPlayerAdapter(mpdServer.getPlayer());
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

    private class MyMpdPlayerAdapter implements MpdPlayerAdapterIF
    {
        public MpdPlayStatusListener playStatusListener = new NullMpdPlayStatusListener();
        private Player player;

        public MyMpdPlayerAdapter(Player player)
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
            mpdServer.setVolume(volume);
        }

        public Integer getVolume()
        {
            return mpdServer.getVolume();
        }

        public Boolean toggleMute()
        {
            return mpdServer.toggleMute();
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
            mpdServer.addVolumeListener(new VolumeListener()
            {
                public void volumeChanged(Integer volume)
                {
                    listener.volumeUpdated(volume);
                }
            });
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
    }
}
