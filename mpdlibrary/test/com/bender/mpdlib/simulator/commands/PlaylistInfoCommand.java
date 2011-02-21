package com.bender.mpdlib.simulator.commands;

import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.commands.Response;

import java.util.List;

/**
 */
public class PlaylistInfoCommand extends SimCommand
{
    @Override
    public void run(String[] commands) throws Exception
    {
        int beginRange = 0, endRange = playlist.size();
        if (commands.length == 2)
        {
            try
            {
                int songPosition = Integer.parseInt(commands[1]);
                beginRange = songPosition;
                endRange = songPosition + 1;
            } catch (NumberFormatException e)
            {
                String[] strings = commands[1].split(":");
                if (strings.length != 2)
                {
                    printError();
                    return;
                }
                try
                {
                    beginRange = Integer.parseInt(strings[0]);
                    endRange = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e1)
                {
                    printError();
                    return;
                }
            }
        }

        if (beginRange < 0 || endRange < 0)
        {
            printError();
            return;
        }
        if (beginRange > playlist.size() || endRange > playlist.size())
        {
            printError();
            return;
        }

        List<SongInfo> playlistInfo = playlist.getPlaylistInfo(beginRange, endRange);
        for (SongInfo songInfo : playlistInfo)
        {
            for (SongInfo.SongAttributeType songAttributeType : SongInfo.SongAttributeType.values())
            {
                if (songAttributeType.equals(SongInfo.SongAttributeType.Id)) continue;
                String value = songInfo.getValue(songAttributeType);
                if (value != null)
                {
                    printWriter.println(songAttributeType + ": " + value);
                }
            }
            // print id last
            SongInfo.SongAttributeType songAttributeType = SongInfo.SongAttributeType.Id;
            printWriter.println(songAttributeType + ": " + songInfo.getValue(songAttributeType));
        }
        printWriter.println(Response.OK);
    }

    private void printError()
    {
        printWriter.println(Response.ACK + "[2@0] {playlistinfo} need a range");
    }
}
