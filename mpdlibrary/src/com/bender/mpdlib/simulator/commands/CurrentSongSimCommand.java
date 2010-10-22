package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.commands.Response;

import java.io.BufferedWriter;

/**
 * todo: replace with documentation
 */
public class CurrentSongSimCommand extends SimCommand
{
    public CurrentSongSimCommand(BufferedWriter simBufferedWriter)
    {
        super(simBufferedWriter);
    }

    @Override
    public void run() throws Exception
    {
        SongInfo currentSong = Playlist.getCurrentSong();
        for (SongInfo.SongAttributeType songAttributeType : SongInfo.SongAttributeType.values())
        {
            String value = currentSong.getValue(songAttributeType);
            if (value != null)
            {
                writer.write(songAttributeType + ": " + value);
            }
        }
        System.out.println(getClass().getSimpleName() + ": id=" + currentSong.getValue(SongInfo.SongAttributeType.Id));
        writer.write(Response.OK.toString());
    }

}
