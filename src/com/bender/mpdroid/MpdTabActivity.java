package com.bender.mpdroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 */
public class MpdTabActivity extends TabActivity
{
    private int PLAYER_TAB_INDEX;
    private int PLAYLIST_TAB_INDEX;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabmain);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        int tabIndex = 0;

        intent = new Intent().setClass(this, MpDroidActivity.class);
        spec = tabHost.newTabSpec("player").setIndicator("Player",
                resources.getDrawable(R.drawable.ic_tab_playback_unselected)).setContent(intent);
        tabHost.addTab(spec);
        PLAYER_TAB_INDEX = tabIndex++;

        intent = new Intent().setClass(this, PlaylistActivity.class);
        spec = tabHost.newTabSpec("playlist").setIndicator("Playlist",
                resources.getDrawable(R.drawable.ic_tab_playlists_unselected)).setContent(intent);
        tabHost.addTab(spec);
        PLAYLIST_TAB_INDEX = tabIndex++;

        tabHost.setCurrentTab(0);
    }

    public int playerTabIndex()
    {
        return PLAYER_TAB_INDEX;
    }

    public int playlistTabIndex()
    {
        return PLAYLIST_TAB_INDEX;
    }

    public void switchTab(int tabIndex)
    {
        getTabHost().setCurrentTab(tabIndex);
    }
}
