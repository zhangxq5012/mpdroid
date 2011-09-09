package com.bender.mpdlib.simulator;

import com.bender.mpdlib.MpdStatus;
import com.bender.mpdlib.PlayStatus;
import com.bender.mpdlib.SongInfo;
import com.bender.mpdlib.Subsystem;
import com.bender.mpdlib.commands.StatusTuple;
import com.bender.mpdlib.simulator.library.Playlist;
import com.bender.mpdlib.util.Log;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 */
public class SimPlayer {
    private static final long INTERVAL = 1000L;
    private AtomicReference<PlayStatus> currentPlayStatus = new AtomicReference<PlayStatus>(PlayStatus.Stopped);
    private AtomicInteger currentVolume = new AtomicInteger(100);
    private SubSystemSupport subSystemSupport;
    private AtomicInteger songProgress;
    private Playlist playlist;
    private Timer songTimer;

    public SimPlayer(SubSystemSupport subSystemSupport, Playlist playlist) {
        this.subSystemSupport = subSystemSupport;
        this.playlist = playlist;
        songProgress = new AtomicInteger(0);
    }

    public void updatePlayStatus(PlayStatus playStatus) {
        PlayStatus oldValue = currentPlayStatus.getAndSet(playStatus);
        if (!oldValue.equals(playStatus)) {
            processPlayStatus(playStatus);
            subSystemSupport.updateSubSystemChanged(Subsystem.player);
        }
    }

    private void processPlayStatus(PlayStatus playStatus) {
        switch (playStatus) {
            case Playing:
                synchronized (this) {
                    if (songTimer == null) {
                        songTimer = new Timer("SongTimer");
                        songTimer.scheduleAtFixedRate(new SongTimerTask(), INTERVAL, INTERVAL);
                    }
                }
                break;

            case Stopped:
                resetProgress();
            case Paused:
                synchronized (this) {
                    if (songTimer != null) {
                        songTimer.cancel();
                        songTimer = null;
                    }
                }
                break;
        }
    }

    private StatusTuple getPlayStatus() {
        return new StatusTuple(MpdStatus.state, currentPlayStatus.get().serverString);
    }

    public PlayStatus getCurrentPlayStatus() {
        return currentPlayStatus.get();
    }

    public Integer getVolume() {
        return currentVolume.get();
    }

    public boolean setVolume(Integer volume) {
        if (volume == null) {
            throw new IllegalArgumentException("Null volume");
        }
        if (volume > 100) {
            volume = 100;
        }
        if (volume < 0) {
            volume = 0;
        }
        Integer oldVolume = currentVolume.getAndSet(volume);
        boolean changed = !oldVolume.equals(volume);
        if (changed) {
            subSystemSupport.updateSubSystemChanged(Subsystem.mixer);
        }
        return changed;
    }

    private StatusTuple getVolumeStatus() {
        return new StatusTuple(MpdStatus.volume, currentVolume.toString());
    }

    public void setSongProgress(int i) {
        songProgress.set(i);
    }

    private StatusTuple getTimeStatus() {
        String totalTime = getCurrentSongTotalTime();
        if (totalTime == null) {
            totalTime = BigInteger.ZERO.toString();
        }
        int currentTime = songProgress.get();
        return new StatusTuple(MpdStatus.time, currentTime + ":" + totalTime);
    }

    public void next() {
        resetProgress();
    }

    public void previous() {
        resetProgress();
    }

    public void resetProgress() {
        songProgress.set(0);
    }

    public void seek(Integer songId, Integer position) {
        playlist.gotoSongBySongId(songId);
        String currentSongTotalTime = getCurrentSongTotalTime();
        // todo: potential threading problem between get and set
        int progress = songProgress.get();
        if (currentSongTotalTime != null && (progress <= Integer.parseInt(currentSongTotalTime))) {
            Log.v(getClass().getSimpleName(), "seekid: " + position + ", previous=" + progress);
            songProgress.set(position);
            subSystemSupport.updateSubSystemChanged(Subsystem.player);
        } else {
            Log.d(getClass().getSimpleName(), "seekid: not seeking. total=" + currentSongTotalTime + ", progress=" + progress);
        }
    }

    private String getCurrentSongTotalTime() {
        return playlist.getCurrentSong().getValue(SongInfo.SongAttributeType.Time);
    }

    public List<StatusTuple> getStatusList() {
        ArrayList<StatusTuple> statusTuples = new ArrayList<StatusTuple>();
        statusTuples.add(getPlayStatus());
        statusTuples.add(getTimeStatus());
        statusTuples.add(getVolumeStatus());
        return statusTuples;
    }

    private class SongTimerTask extends TimerTask {
        @Override
        public void run() {
            int progress = songProgress.incrementAndGet();
            String totalTimeString = getCurrentSongTotalTime();
            if (totalTimeString != null && progress >= Integer.parseInt(totalTimeString)) {
                // song finished
                resetProgress();
                playlist.next();
            }
            Log.v("SimPlayer", "timer fired: " + progress);
        }
    }
}
