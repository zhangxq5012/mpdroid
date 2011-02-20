package com.bender.mpdroid.mpdService;

/**
 */
public class NullMpdOptions implements MpdOptionsIF
{
    public void toggleRepeat()
    {
    }

    public Boolean getRepeat()
    {
        return false;
    }

    public void addOptionsListener(OptionsListener optionsListener)
    {
    }

    public void toggleRandom()
    {
    }

    public Boolean getRandom()
    {
        return false;
    }
}
