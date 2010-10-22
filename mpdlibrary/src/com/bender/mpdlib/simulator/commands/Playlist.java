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
    private static SongInfo currentSong;

    private static List<SongInfo> library;

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
        currentSong = library.get(0);
    }

    public static SongInfo getCurrentSong()
    {
        return currentSong;
    }

    public static StatusTuple getStatus()
    {
        return new StatusTuple(MpdStatus.songid, currentSong.getValue(SongInfo.SongAttributeType.Id));
    }

    public static void next()
    {
        int index = Integer.parseInt(currentSong.getValue(SongInfo.SongAttributeType.Id));
        if (index >= library.size())
        {
            index = 0;
        }
        currentSong = library.get(index);
        IdleSimCommand.subsystemUpdated(Subsystem.player);
    }
}
