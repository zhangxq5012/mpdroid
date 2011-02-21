package com.bender.mpdroid;

import android.app.ListActivity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bender.mpdroid.mpdService.MpdPlaylistAdapterIF;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;

/**
 */
public class PlaylistActivity extends ListActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        setListAdapter(new ArrayAdapter<String>(this, R.layout.playlist_item,
//                new String[]{"a", "b", "c"}));
        // todo: get playlist from mpd service
        setListAdapter(new PlaylistAdapter());

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class PlaylistAdapter implements ListAdapter
    {
        private LayoutInflater mInflater;

        public PlaylistAdapter()
        {
            mInflater = PlaylistActivity.this.getLayoutInflater();
        }

        public boolean areAllItemsEnabled()
        {
            return false;
        }

        public boolean isEnabled(int i)
        {
            return false;
        }

        public void registerDataSetObserver(DataSetObserver dataSetObserver)
        {
        }

        public void unregisterDataSetObserver(DataSetObserver dataSetObserver)
        {
        }

        public int getCount()
        {
            MpdServiceAdapterIF mpdService = MpDroidActivity.getMpdService();
            MpdPlaylistAdapterIF playlist = mpdService.getPlaylist();
            return playlist.getPlaylistSize();
        }

        public Object getItem(int i)
        {
            return "text " + i;
        }

        public long getItemId(int i)
        {
            return 0;
        }

        public boolean hasStableIds()
        {
            return true;
        }

        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = mInflater.inflate(R.layout.playlist_item, viewGroup, false);
            }
            String text = (String) getItem(i);
            ((TextView) view).setText(text);
            return view;
        }

        public int getItemViewType(int i)
        {
            return 0;
        }

        public int getViewTypeCount()
        {
            return 1;
        }

        public boolean isEmpty()
        {
            return getCount() == 0;
        }
    }
}
