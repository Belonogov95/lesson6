package ru.ya.rss2;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by vanya on 17.12.14.
 */
public class MyIntentService extends IntentService {
    public static final String NOTIFICATION = "MyNotification_42";
    public MyIntentService() {   super("MyIntentService"); }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent result = new Intent(NOTIFICATION);
        try {
            Log.e("start ", "Intent");
            URL url = new URL(intent.getStringExtra(RSSSQLiteHelper.COLUMN_URL));
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();

            /// get encoding
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader2.readLine();
            int t = line.indexOf("encoding");
            String endCoding = "";
            t += 9;
            assert (line.charAt(t) == '"');
            t++;
            for (; line.charAt(t) != '"'; t++)
                endCoding += line.charAt(t);

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            Reader reader = new InputStreamReader(inputStream, endCoding);
            InputSource is = new InputSource(reader);

            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            MyHandler myHandler = new MyHandler();
            saxParser.parse(is, myHandler);

            RSSFeed rssFeed = myHandler.getRSSFeed();

            String selection = RSSSQLiteHelper.COLUMN_ADDRESS + "=?";
            String[] selectionArgs = {url.toString()};
            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(RSSContentProvider.CONTENT_URI, selection, selectionArgs);

            Log.e("in Intent rssSize", "" + rssFeed.arrayList.size());

            //for (OnePost onePost// : rssFeed.arrayList) {
            int sz = rssFeed.arrayList.size();
            //for (OnePost x: rssFeed.arrayList)
                    //Log.e("pointer: ", "" + x);
            //int sz = 10;
            ContentValues [] contentArray = new ContentValues[sz];
            //String[] tt = new String[1];
            //Log.e("DEBUG: ", String.valueOf(tt[0]));
            //tt[0] = "sdf";
            //Log.e("DEBUG: ", String.valueOf(tt[0]));
            for (int i = 0; i < sz; i++) {
                //Log.e("i:", "" + i);
                OnePost onePost = rssFeed.arrayList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(RSSSQLiteHelper.COLUMN_ADDRESS, url.toString());
                contentValues.put(RSSSQLiteHelper.COLUMN_TITLE, onePost.title);
                //contentValues.put(RSSSQLiteHelper.COLUMN_LINK, onePost.link.toString());
                contentValues.put(RSSSQLiteHelper.COLUMN_LINK, String.valueOf(onePost.link));
                contentValues.put(RSSSQLiteHelper.COLUMN_DESCRIPTION, onePost.description);
                contentValues.put(RSSSQLiteHelper.COLUMN_DATE, onePost.date);
                contentArray[i] = contentValues;
                //contentResolver.insert(RSSContentProvider.CONTENT_URI, contentValues);
                //SystemClock.sleep(1000);
            }
            contentResolver.bulkInsert(RSSContentProvider.CONTENT_URI, contentArray);
            //Intent intent1 = new Intent(NOTIFICATION);
            //sendBroadcast(intent1);
            return;
        } catch (MalformedURLException e) {
            Log.e("bug:", "1");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e("bug:", "2");
            e.printStackTrace();
        } catch (IOException e) {
            sendBroadcast(result);
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Log.e("bug:", "4");
            e.printStackTrace();
        } catch (SAXException e) {
            Log.e("bug:", "5");
            e.printStackTrace();
        }
        Log.e("finish:", "something bad");
    }
}
