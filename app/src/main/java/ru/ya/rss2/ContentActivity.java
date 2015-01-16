package ru.ya.rss2;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by vanya on 17.12.14.
 */
public class ContentActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private SimpleCursorAdapter adapter;
    private String url;
    Loader < Cursor > myLoader;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "incorrect URL", Toast.LENGTH_LONG).show();
            Log.e("receive", "broadcast");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        Intent intent = getIntent();
        url = intent.getStringExtra(RSSSQLiteHelper.COLUMN_URL);
        String newTitle = getResources().getString(R.string.refresh_channel) + "\n" + url;
        ((Button)findViewById(R.id.refresh_channel)).setText(newTitle);
        Log.e("success", url);
        fillDate();
        Log.e("success", "finish");


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

    public void onClickRefreshChannel(View view) {
        Intent intent = new Intent(ContentActivity.this, MyIntentService.class);
        intent.putExtra(RSSSQLiteHelper.COLUMN_URL, url);
        Toast toast = Toast.makeText(this, "Refreshing", Toast.LENGTH_LONG);
        toast.show();
        Log.e("after: ", "intent");
        startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection = RSSSQLiteHelper.COLUMN_ADDRESS + "=?";
        String [] selectionArgs = {url};
        return new CursorLoader(this, RSSContentProvider.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.e("onLoadFinished--------:", "start");
        if (adapter.getCursor() == null) Log.e("before swap", "null");
        else Log.e("before swap", adapter.getCursor().getCount() + "");
        adapter.swapCursor(cursor);


//        Log.e("getCursor", "" + adapter.getCursor());
//        if (cursor.getCount() == 0) {
//            Log.e("getCursor", "" + adapter.getCursor());
//            //Log.e("sdf", "tut");
//            Toast.makeText(this, "Refreshing", Toast.LENGTH_LONG).show();
//            Intent intentService = new Intent(this, MyIntentService.class);
//            intentService.putExtra(RSSSQLiteHelper.COLUMN_URL, url);
//            startService(intentService);
//
//        }

        Log.e("after swap", adapter.getCursor().getCount() + "");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, WebActivity.class);
        Cursor cursor = ((SimpleCursorAdapter)getListAdapter()).getCursor();
        cursor.moveToPosition(position);
        intent.putExtra(RSSSQLiteHelper.COLUMN_URL, cursor.getString(cursor.getColumnIndex(RSSSQLiteHelper.COLUMN_LINK)));
        startActivity(intent);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    private void fillDate() {
        String[] from = {RSSSQLiteHelper.COLUMN_TITLE, RSSSQLiteHelper.COLUMN_DATE, RSSSQLiteHelper.COLUMN_DESCRIPTION};
        int[] to = {R.id.titleView, R.id.dateView, R.id.descriptionView};
        myLoader = getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.one_post, null, from, to, 0);
        setListAdapter(adapter);
    }

}
