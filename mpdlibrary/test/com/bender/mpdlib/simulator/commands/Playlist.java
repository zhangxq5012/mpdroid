package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * todo: replace with documentation
 */
public class Playlist
{
    private SongInfo currentSong;

    private static List<SongInfo> library;
    private SubSystemSupport subSystemSupport;

    static
    {
        library = new ArrayList<SongInfo>();
        for (int i = 0; i < 10; i++)
        {
            Integer id = i + 1;
            SongInfo songInfo = new SongInfo();
            songInfo.updateValue(SongInfo.SongAttributeType.Title, "Simulator Song Title " + id);
            songInfo.updateValue(SongInfo.SongAttributeType.Id, id.toString());
            library.add(songInfo);
        }
    }

    public Playlist(SubSystemSupport subSystemSupport)
    {
        this.subSystemSupport = subSystemSupport;
        currentSong = library.get(0);
    }

    public SongInfo getCurrentSong()
    {
        return currentSong;
    }

    public StatusTuple getStatus()
    {
        return new StatusTuple(MpdStatus.songid, currentSong.getValue(SongInfo.SongAttributeType.Id));
    }

    public void next()
    {
        int index = Integer.parseInt(currentSong.getValue(SongInfo.SongAttributeType.Id));
        gotoSongIndex(index);
    }

    private void gotoSongIndex(int index)
    {
        if (index >= library.size())
        {
            index = 0;
        }
        else if (index < 0)
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
}
