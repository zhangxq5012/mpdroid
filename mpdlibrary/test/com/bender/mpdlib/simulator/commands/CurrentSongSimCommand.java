package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.commands.Response;
import com.bender.mpdlib.util.Log;

/**
 */
public class CurrentSongSimCommand extends SimCommand
{

    @Override
    public void run(String[] commands) throws Exception
    {
        SongInfo currentSong = playlist.getCurrentSong();
        for (SongInfo.SongAttributeType songAttributeType : SongInfo.SongAttributeType.values())
        {
            String value = currentSong.getValue(songAttributeType);
            if (value != null)
            {
                printWriter.println(songAttributeType + ": " + value);
            }
        }
        Log.v(getClass().getSimpleName(), ": id=" + currentSong.getValue(SongInfo.SongAttributeType.Id));
        printWriter.println(Response.OK);
    }

}
