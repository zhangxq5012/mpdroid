package com.bender.mpdlib;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class SongInfo {
    private Map<SongAttributeType, String> valueMap = new HashMap<SongAttributeType, String>(SongAttributeType.values().length);

    public static final String ANY = "any";

    public static enum SongAttributeType {
        file,
        Title,
        Artist,
        Album,
        Date,
        Genre,
        Time,
        Pos,
        Id;

        public static SongAttributeType parse(String line) {
            line = line.toLowerCase();
            for (SongAttributeType songAttributeType : values()) {
                if (songAttributeType.name().toLowerCase().equals(line)) {
                    return songAttributeType;
                }
            }
            return null;
        }
    }

    public String getValue(SongAttributeType songAttributeType) {
        return valueMap.get(songAttributeType);
    }

    public void updateValue(String line) {
        String[] strings = line.split(":");
        SongAttributeType songAttributeType = SongAttributeType.parse(strings[0].trim());
        String stringValue = strings[1].trim();
        updateValue(songAttributeType, stringValue);
    }

    public void updateValue(SongAttributeType songAttributeType, Object value) {
        if (value != null) {
            valueMap.put(songAttributeType, value.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SongInfo songInfo = (SongInfo) o;

        if (valueMap != null ? !valueMap.equals(songInfo.valueMap) : songInfo.valueMap != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return valueMap != null ? valueMap.hashCode() : 0;
    }
}
