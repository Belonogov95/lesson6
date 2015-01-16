package ru.ya.rss2;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.net.URL;
import java.util.Arrays;

/**
 * Created by vanya on 09.12.14.
 */
public class RSSContentProvider extends ContentProvider {
    private static final String AUTHORITY = "ru.ya.rss2.RSSContentProvider";
    private static final String CHANNELS_PATH = "PCHANNELS";
    private static final String CONTENT_PATH = "PCONTENT";
    /// "content://ru.ys.rss2.contentProvider/newFeed?feedXml=" + Uri.encode("http://bash.im/feed")
    private Boolean flagUpdate = true;

    public static final Uri CHANNELS_URI = Uri.parse("content://" + AUTHORITY + "/" + CHANNELS_PATH);
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CONTENT_PATH);
    private static final UriMatcher uriMatcher = new UriMatcher(0);

    static {
        uriMatcher.addURI(AUTHORITY, CHANNELS_PATH, 1);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, 2);
    }


    RSSSQLiteHelper rsssQLiteHelper;

    @Override
    public boolean onCreate() {
        rsssQLiteHelper = new RSSSQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
//        Log.e("query:", uri.toString());
//        Log.e("projection", Arrays.toString(projection));
//        Log.e("selection: ", String.valueOf(selection));
//        Log.e("selectionARGS: ", Arrays.toString(selectionArgs));
//        Log.e("sortOrder: ", String.valueOf(sortOrder));

        if (uriMatcher.match(uri) == 1) {
            //Log.e("before fail 1", "-----------------------");
            cursor = rsssQLiteHelper.getReadableDatabase().query(RSSSQLiteHelper.CHANNELS_TABLE,
                    projection, selection, selectionArgs, null, null, sortOrder);
            //Log.e("after fail", "1 -----------");
        }
        else if (uriMatcher.match(uri) == 2) {
            //Log.e("before fail", "-----------------------");
            cursor = rsssQLiteHelper.getReadableDatabase().query(RSSSQLiteHelper.CONTENT_TABLE,
                    projection, selection, selectionArgs, null, null, sortOrder);
        }
        else {
            throw new Error("assert");
        }
        //Log.e("before close ", "a");
        //Log.e("after close ", "a");
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private void upd(Uri uri) {
        if (flagUpdate)
            getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id;
        SQLiteDatabase sqLiteDatabase = rsssQLiteHelper.getWritableDatabase();
        if (uriMatcher.match(uri) == 1)
            id = sqLiteDatabase.insert(RSSSQLiteHelper.CHANNELS_TABLE, null, contentValues);
        else if (uriMatcher.match(uri) == 2)
            id = sqLiteDatabase.insert(RSSSQLiteHelper.CONTENT_TABLE, null, contentValues);
        else {
            sqLiteDatabase.close();
            throw new Error("assert");
        }
        sqLiteDatabase.close();

        upd(uri);

        return Uri.parse(CHANNELS_PATH + "/" + id);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        flagUpdate = false;
        for (ContentValues x: values) {
            insert(uri, x);
        }
        flagUpdate = true;
        upd(uri);
        return -1;
    }
/*public Uri insertAll(Uri uri, ContentValues[] contentValues) {

    }*/

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int rowsDeleted;
        SQLiteDatabase sqLiteDatabase = rsssQLiteHelper.getWritableDatabase();
        if (uriMatcher.match(uri) == 1)
            rowsDeleted = sqLiteDatabase.delete(RSSSQLiteHelper.CHANNELS_TABLE, s, strings);
        else if (uriMatcher.match(uri) == 2)
            rowsDeleted = sqLiteDatabase.delete(RSSSQLiteHelper.CONTENT_TABLE, s, strings);
        else {
            sqLiteDatabase.close();
            throw new Error("assert");
        }
        sqLiteDatabase.close();

        getContext().getContentResolver().notifyChange(uri, null);
        //Log.e("rows Del", "" + rowsDeleted);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int rowsUpdated;
        SQLiteDatabase sqLiteDatabase = rsssQLiteHelper.getWritableDatabase();
        if (uriMatcher.match(uri) == 1)
            rowsUpdated = sqLiteDatabase.update(RSSSQLiteHelper.CHANNELS_TABLE, contentValues, s, strings);
        else if (uriMatcher.match(uri) == 2)
            rowsUpdated = sqLiteDatabase.update(RSSSQLiteHelper.CONTENT_TABLE, contentValues, s, strings);
        else {
            sqLiteDatabase.close();
            throw new Error("assert");
        }
        sqLiteDatabase.close();
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
