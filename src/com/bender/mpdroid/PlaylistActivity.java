package com.bender.mpdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bender.mpdroid.mpdService.*;

/**
 */
public class PlaylistActivity extends ListActivity
{
    private PlaylistAdapter playlistAdapter;
    private MpdPlaylistAdapterIF mpdPlaylistAdapterIF;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final MpdServiceAdapterIF mpdService = MpDroidActivity.getMpdService();
        mpdPlaylistAdapterIF = mpdService.getPlaylist();
        playlistAdapter = new PlaylistAdapter();
        mpdService.addConnectionListener(new MpdConnectionListenerIF()
        {
            public void connectionStateUpdated(boolean connected)
            {
                mpdPlaylistAdapterIF = mpdService.getPlaylist();
                playlistAdapter.updatePlaylist();
            }
        });
        setListAdapter(playlistAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int songPos, long l)
            {
                mpdPlaylistAdapterIF.play(songPos);
                MpdTabActivity mpdTabActivity = (MpdTabActivity) getParent();
                mpdTabActivity.switchTab(mpdTabActivity.playerTabIndex());
            }
        });
    }

    private class PlaylistAdapter extends BaseAdapter
    {
        private LayoutInflater mInflater;

        public PlaylistAdapter()
        {
            mInflater = PlaylistActivity.this.getLayoutInflater();
        }

        public int getCount()
        {
            return mpdPlaylistAdapterIF.getPlaylistSize();
        }

        public Object getItem(int i)
        {
            return new SongNameDecorator(mpdPlaylistAdapterIF.getSongInfo(i));
        }

        public long getItemId(int i)
        {
            return 0;
        }

        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.playlist_item, viewGroup, false);
            }
            MpdSongAdapterIF song = (MpdSongAdapterIF) getItem(i);
            ((TextView) view).setText(song.getSongName());
            return view;
        }

        public void updatePlaylist()
        {
            Runnable runnable = new Runnable()
            {
                public void run()
                {
                    notifyDataSetChanged();
                }
            };
            runOnUiThread(runnable);
        }
    }
}
