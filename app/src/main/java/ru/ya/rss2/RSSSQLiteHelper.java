package ru.ya.rss2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by vanya on 09.12.14.
 */
public class RSSSQLiteHelper extends SQLiteOpenHelper {
    public static final String CREATE_TABLE = "CREATE TABLE";
    public static final String DROP_TABLE = "DROP TABLE";

    public static final String CHANNELS_TABLE = "CHANNELS";
    public static final String CONTENT_TABLE = "CONTENT";

    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ADDRESS = "ADDRESS";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_LINK = "LINK";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_DATE = "DATE";


    public RSSSQLiteHelper(Context context) {
        super(context, "RSSDataBase", null, 14);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE + " "
                + CHANNELS_TABLE
                + "("
                + COLUMN_URL
                + " text UNIQUE, "
                + COLUMN_ID
                + " integer primary key autoincrement); ");

        sqLiteDatabase.execSQL(CREATE_TABLE + " "
                + CONTENT_TABLE + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_ADDRESS + " text, "
                + COLUMN_TITLE + " text, "
                + COLUMN_LINK + " text, "
                + COLUMN_DESCRIPTION + " text, "
                + COLUMN_DATE + " text); ");
        //Log.e("success content", "!!!!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(DROP_TABLE + " " + CHANNELS_TABLE);
        sqLiteDatabase.execSQL(DROP_TABLE + " " + CONTENT_TABLE);
        onCreate(sqLiteDatabase);
    }
}
