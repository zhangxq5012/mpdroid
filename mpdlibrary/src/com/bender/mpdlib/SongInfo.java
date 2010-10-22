package com.bender.mpdlib;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * todo: replace with documentation
 */
public class SongInfo
{
    private Map<SongAttributeType, String> valueMap = new HashMap<SongAttributeType, String>(SongAttributeType.values().length);

    public static enum SongAttributeType
    {
        file,
        Title,
        //        Name,
        Artist,
        Date,
        Genre,
        Time,
        Pos,
        Id;

        public static SongAttributeType parse(String line)
        {
            for (SongAttributeType songAttributeType : values())
            {
                if (songAttributeType.name().equals(line))
                {
                    return songAttributeType;
                }
            }
            return null;
        }
    }

    public String getValue(SongAttributeType songAttributeType)
    {
        return valueMap.get(songAttributeType);
    }

    public void updateValue(String line)
    {
        StringTokenizer stringTokenizer = new StringTokenizer(line, ":");
        SongAttributeType songAttributeType = SongAttributeType.parse(stringTokenizer.nextToken());
        String stringValue = stringTokenizer.nextToken().trim();
        updateValue(songAttributeType, stringValue);
    }

    public void updateValue(SongAttributeType songAttributeType, String value)
    {
        valueMap.put(songAttributeType, value);
    }
}
