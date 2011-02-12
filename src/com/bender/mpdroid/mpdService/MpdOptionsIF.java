package com.bender.mpdroid.mpdService;

/**
 */
public interface MpdOptionsIF
{
    void toggleRepeat();

    Boolean getRepeat();

    void addOptionsListener(OptionsListener optionsListener);
}
