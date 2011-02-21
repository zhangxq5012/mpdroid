package com.bender.mpdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bender.mpdroid.mpdService.MpdPlaylistAdapterIF;
import com.bender.mpdroid.mpdService.MpdServiceAdapterIF;
import com.bender.mpdroid.mpdService.MpdSongAdapterIF;
import com.bender.mpdroid.mpdService.SongNameDecorator;

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
        setListAdapter(new PlaylistAdapter());

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int songPos, long l)
            {
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                MpDroidActivity.getMpdService().getPlaylist().play(songPos);
                //todo: jump to player frame
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
            MpdServiceAdapterIF mpdService = MpDroidActivity.getMpdService();
            MpdPlaylistAdapterIF playlist = mpdService.getPlaylist();
            return playlist.getPlaylistSize();
        }

        public Object getItem(int i)
        {
            MpdServiceAdapterIF mpdService = MpDroidActivity.getMpdService();
            MpdPlaylistAdapterIF playlist = mpdService.getPlaylist();
            return new SongNameDecorator(playlist.getSongInfo(i));
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
    }
}
