package com.bender.mpdlib;

/**
 * Value "0" = false
 * Value "1" = true
 */
public class MpdBoolean
{
    /**
     * Parse the text value into a boolean or throws a NumberFormatException.
     *
     * @param strValue 0 = false , 1 = true
     * @return boolean value
     * @throws NumberFormatException if the string is not a number
     */
    public static boolean parseString(String strValue) throws NumberFormatException
    {
        int intValue = Integer.parseInt(strValue);
        return intValue != 0;
    }

    /**
     * Convert the boolean into it's Mpd boolean text value.
     *
     * @param boolValue boolean value
     * @return mpd boolean string
     */
    public static String toString(boolean boolValue)
    {
        return boolValue ? "1" : "0";
    }
}
