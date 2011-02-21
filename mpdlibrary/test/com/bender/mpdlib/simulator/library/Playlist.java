package com.bender.mpdlib.simulator.library;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.simulator.SubSystemSupport;
import com.bender.mpdlib.util.Log;
import noNamespace.LibraryDocument;
import noNamespace.SongType;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class Playlist
{
    public static final String TAG = Playlist.class.getSimpleName();
    private SongInfo currentSong;

    private List<SongInfo> library;
    private SubSystemSupport subSystemSupport;
    private boolean repeat;
    private boolean random;


    public Playlist(SubSystemSupport subSystemSupport)
    {
        library = new ArrayList<SongInfo>();
        URL libraryXml = getClass().getResource("/com/bender/mpdlib/simulator/library/library.xml");
        try
        {
            LibraryDocument libraryDocument = LibraryDocument.Factory.parse(libraryXml);
            SongType[] songs = libraryDocument.getLibrary().getSongArray();
            for (SongType song : songs)
            {
                SongInfo songInfo = new SongInfo();
                updateSong(songInfo, song);
                library.add(songInfo);
            }
            Log.v(TAG, "Loaded library. (" + library.size() + ") entries");
        } catch (Exception e)
        {
            Log.e(TAG, "Error parsing library xml", e);
        }
        this.subSystemSupport = subSystemSupport;
        if (!library.isEmpty())
        {
            currentSong = library.get(0);
        }
    }

    private static void updateSong(SongInfo songInfo, SongType song)
    {
        String artist = song.getArtist();
        songInfo.updateValue(SongInfo.SongAttributeType.Artist, artist);
        String file = song.getFile();
        songInfo.updateValue(SongInfo.SongAttributeType.file, file);
        BigInteger id = song.getId();
        songInfo.updateValue(SongInfo.SongAttributeType.Id, id);
        BigInteger date = song.getDate();
        songInfo.updateValue(SongInfo.SongAttributeType.Date, date);
        String genre = song.getGenre();
        songInfo.updateValue(SongInfo.SongAttributeType.Genre, genre);
        BigInteger pos = song.getPos();
        songInfo.updateValue(SongInfo.SongAttributeType.Pos, pos);
        BigInteger time = song.getTime();
        songInfo.updateValue(SongInfo.SongAttributeType.Time, time);
        String title = song.getTitle();
        songInfo.updateValue(SongInfo.SongAttributeType.Title, title);
    }

    public SongInfo getCurrentSong()
    {
        return currentSong;
    }

    private StatusTuple getCurrentSongStatus()
    {
        if (currentSong != null)
        {
            return new StatusTuple(MpdStatus.songid, currentSong.getValue(SongInfo.SongAttributeType.Id));
        } else
        {
            return null;
        }
    }

    public void next()
    {
        int index = Integer.parseInt(currentSong.getValue(SongInfo.SongAttributeType.Id));
        gotoSongIndex(index);
    }

    /**
     * The simulator makes the very important assumption that the song id's in the xml file are sequential.
     *
     * @param index
     */
    private void gotoSongIndex(int index)
    {
        if (index >= library.size())
        {
            index = 0;
        } else if (index < 0)
        {
            index = library.size() - 1;
        }
        currentSong = library.get(index);
        subSystemSupport.updateSubSystemChanged(Subsystem.player);
    }

    public void previous()
    {
        int index = Integer.parseInt(currentSong.getValue(SongInfo.SongAttributeType.Id));
        gotoSongIndex(index - 2);
    }

    public int size()
    {
        return library.size();
    }

    public void addSong(SongInfo songInfo)
    {
        library.add(songInfo);
    }

    public void gotoSongBySongId(Integer songId)
    {
        Integer currentSongId = Integer.valueOf(currentSong.getValue(SongInfo.SongAttributeType.Id));
        if (!currentSongId.equals(songId))
        {
            gotoSongIndex(songId - 1);
        }
    }

    public void setRepeat(boolean repeat)
    {
        //todo: use repeat
        this.repeat = repeat;
    }

    public void setRandom(boolean random)
    {
        //todo: use random
        this.random = random;
    }

    public List<StatusTuple> getStatusList()
    {
        List<StatusTuple> statusTuples = new ArrayList<StatusTuple>();
        statusTuples.add(getCurrentSongStatus());
        statusTuples.add(getPlaylistLengthStatus());
        return statusTuples;
    }

    private StatusTuple getPlaylistLengthStatus()
    {
        return new StatusTuple(MpdStatus.playlistlength, Integer.toString(size()));
    }

    public List<SongInfo> getPlaylistInfo(int beginRange, int endRange)
    {
        if (beginRange == -1 && endRange == -1)
        {
            beginRange = 0;
            endRange = size();
        }
        return library.subList(beginRange, endRange);
    }
}
