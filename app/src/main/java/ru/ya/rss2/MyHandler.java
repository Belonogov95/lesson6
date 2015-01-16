package ru.ya.rss2;

import android.text.Html;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by vanya on 20.12.14.
 */

public class MyHandler extends DefaultHandler {
    RSSFeed rssFeed = new RSSFeed();
    OnePost onePost;
    int type;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("item"))
            onePost = new OnePost();

        if (qName.equals("link"))
            type = 1;
        if (qName.equals("title"))
            type = 2;
        if (qName.equals("pubDate"))
            type = 3;
        if (qName.equals("description"))
            type = 4;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("item")) {
            //Log.e("abacaba", onePost.description);
            rssFeed.arrayList.add(onePost);
            onePost = null;
        }
        if (qName.equals("link"))
            type = -1;
        if (qName.equals("title"))
            type = -1;
        if (qName.equals("pubDate"))
            type = -1;
        if (qName.equals("description"))
            type = -1;
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (onePost == null)
            return;

        String s = new String(ch, start, length);
        s = Html.fromHtml(s).toString();
        //Log.e("abacaba", "type: " + type + " " + s);
        if (type == 1) {
            try {
                onePost.link = new URL(s);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (type == 2) {
            onePost.title = s;
        }
        if (type == 3) {
            onePost.date = s;
        }
        if (type == 4)
            onePost.description = s;
    }

    public RSSFeed getRSSFeed() {
        return rssFeed;
    }
}

