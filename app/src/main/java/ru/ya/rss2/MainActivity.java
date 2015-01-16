package ru.ya.rss2;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView textView;
    private SimpleCursorAdapter adapter;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "incorrect URL", Toast.LENGTH_LONG).show();
            Log.e("receive", "broadcast");
            //myLoader.onContentChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.channel_title);
        fillDate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, RSSContentProvider.CHANNELS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    private void fillDate() {
        Log.e("aba", "fillDate");
        String[] from = {RSSSQLiteHelper.COLUMN_URL};
        int[] to = new int[]{R.id.channel_title};
        getLoaderManager().initLoader(0, null, this);
        adapter = new MySimpleCursorAdapter(this, R.layout.channel_row, null, from, to, 0, getContentResolver());
        setListAdapter(adapter);
    }

    public void addChannel(View view) {
        String s = textView.getText().toString();
        textView.setText("");
        if (!s.startsWith("http://")) s = "http://" + s;
        //if (!s.endsWith("/rss")) s += "/rss";
        ContentValues contentValues = new ContentValues();
        contentValues.put(RSSSQLiteHelper.COLUMN_URL, s);

        getContentResolver().insert(RSSContentProvider.CHANNELS_URI, contentValues);

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra(RSSSQLiteHelper.COLUMN_URL, s);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(MyIntentService.NOTIFICATION));
    }
/*private void initOnClick() {
        ListView listview = getListView();
        Log.e("abacaba: ", "initOnClick");
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e("position id: ", "" + position + "  " + id);

                TextView textview = (TextView) view.findViewById(R.id.channel_title);
                String url = textview.toString();
                Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                intent.putExtra(RSSSQLiteHelper.COLUMN_URL, url);
                startActivity(intent);

            }
        });
    }*/
}
