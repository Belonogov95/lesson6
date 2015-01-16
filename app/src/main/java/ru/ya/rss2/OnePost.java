package ru.ya.rss2;

import java.net.URL;

/**
 * Created by vanya on 17.12.14.
 */

public class OnePost {
    String title;
    URL link;
    String description;
    String date;

    public OnePost() {
    }

    public OnePost(String title, URL link, String description, String date) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.date = date;
    }
}




