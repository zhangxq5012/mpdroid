package com.bender.mpdroid;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.bender.mpdlib.util.Log;
import com.bender.mpdroid.mpdService.MpdPlaylistAdapterIF;
import com.bender.mpdroid.mpdService.MpdSongAdapterIF;
import com.bender.mpdroid.mpdService.SongNameDecorator;

import java.util.ArrayList;
import java.util.List;

public class SearchPlaylistActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(getClass().getSimpleName(), "onCreate()");

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            final MpdPlaylistAdapterIF playlist = MpDroidActivity.getMpdService().getPlaylist();
            performQuery(playlist, query);
        }
    }

    private void performQuery(final MpdPlaylistAdapterIF playlist, String query) {
        Log.v(getClass().getSimpleName(), "performQuery(" + query + ")");
        AsyncTask<String, Void, List<MpdSongAdapterIF>> searchTask = new AsyncTask<String, Void, List<MpdSongAdapterIF>>() {
            @Override
            protected List<MpdSongAdapterIF> doInBackground(String... strings) {
                if (strings.length != 1) {
                    Log.e(SearchPlaylistActivity.class.getSimpleName(), "ILLEGAL argument", new RuntimeException());
                    return new ArrayList<MpdSongAdapterIF>(0);
                }
                return playlist.search(strings[0]);
            }

            @Override
            protected void onPostExecute(final List<MpdSongAdapterIF> mpdSongAdapterIFs) {
                Log.v(SearchPlaylistActivity.class.getSimpleName(), "result: " + mpdSongAdapterIFs.size() + " items");
                ListView lv = getListView();
                lv.setTextFilterEnabled(true);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> adapterView, View view, int songPos, long l) {
                        MpdSongAdapterIF mpdSongAdapterIF = mpdSongAdapterIFs.get(songPos);
                        Log.w(SearchPlaylistActivity.class.getSimpleName(), "TODO: play seletected song: " + new SongNameDecorator(mpdSongAdapterIF));
                        // todo: play by song id
                        //todo: how to go to player tab?
                        finish();
                    }
                });
                setListAdapter(new BaseAdapter() {
                    public int getCount() {
                        return mpdSongAdapterIFs.size();
                    }

                    public Object getItem(int i) {
                        return new SongNameDecorator(mpdSongAdapterIFs.get(i));
                    }

                    public long getItemId(int i) {
                        return i;
                    }

                    public View getView(int i, View view, ViewGroup viewGroup) {
                        if (view == null) {
                            view = SearchPlaylistActivity.this.getLayoutInflater().inflate(R.layout.playlist_item, viewGroup, false);
                        }
                        MpdSongAdapterIF song = (MpdSongAdapterIF) getItem(i);
                        ((TextView) view).setText(song.getSongName());
                        return view;
                    }
                });
            }
        };
        searchTask.execute(query);
    }

}
