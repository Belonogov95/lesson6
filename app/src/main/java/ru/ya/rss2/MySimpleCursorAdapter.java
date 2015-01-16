package ru.ya.rss2;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by vanya on 17.12.14.
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {
    Context context;
    Cursor myCursor = null;
    ContentResolver contentResolver;
    String text;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags,
                                 ContentResolver contentResolver) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.contentResolver = contentResolver;
    }

    @Override
    public Cursor swapCursor(Cursor c) {
        myCursor = c;
        return super.swapCursor(c);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.e("LOG", "" + position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        view = (convertView == null)? inflater.inflate(R.layout.channel_row, parent, false): convertView;

        TextView textView = (TextView) view.findViewById(R.id.channel_title);


        int oldPosition = myCursor.getPosition();
        myCursor.moveToPosition(position);
        text = myCursor.getString(myCursor.getColumnIndex(RSSSQLiteHelper.COLUMN_URL));
        textView.setText(text);
        myCursor.moveToPosition(oldPosition);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContentActivity.class);
                TextView textView1 = (TextView)view.findViewById(R.id.channel_title);
                String myText = textView1.getText().toString();

                //String myText= myCursor.
                intent.putExtra(RSSSQLiteHelper.COLUMN_URL, myText);
                context.startActivity(intent);
            }
        });

//        Button buttonClear = (Button)view.findViewById(R.id.clear_button);
//        buttonClear.setTag(text);
//        buttonClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String selection2 = RSSSQLiteHelper.COLUMN_ADDRESS + "=?";
//                String text = (String)view.getTag();
//                String[] selectionArgs2 = {text};
//                contentResolver.delete(RSSContentProvider.CONTENT_URI, selection2, selectionArgs2);
//            }
//        });

        Button buttonRemove = (Button) view.findViewById(R.id.remove_button);
        buttonRemove.setTag(text);
        //button.setOnClickListener(new MyViewOnClick(text, contentResolver));
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = (String)view.getTag();
                String selection = RSSSQLiteHelper.COLUMN_URL + "=?";
                String[] selectionArgs = {text};
                contentResolver.delete(RSSContentProvider.CHANNELS_URI, selection, selectionArgs);
                String selection2 = RSSSQLiteHelper.COLUMN_ADDRESS + "=?";
                String[] selectionArgs2 = {text};
                contentResolver.delete(RSSContentProvider.CONTENT_URI, selection2, selectionArgs2);
            }
        });

        return view;
    }
}
