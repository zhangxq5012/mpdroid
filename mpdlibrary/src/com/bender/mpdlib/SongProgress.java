package com.bender.mpdlib;

/**
 * todo: replace with documentation
 */
public class SongProgress
{
    private int currentTime;
    private int totalTime;

    public SongProgress(int currentTime, int totalTime)
    {
        this.currentTime = currentTime;
        this.totalTime = totalTime;
    }

    public int getCurrentTime()
    {
        return currentTime;
    }

    public int getTotalTime()
    {
        return totalTime;
    }

    public float getPercentCompleteAFloat()
    {
        return ((float) currentTime) / ((float) totalTime) * 100.0f;
    }

    public int getPercentComplete()
    {
        return Math.round(getPercentCompleteAFloat());
    }

}
