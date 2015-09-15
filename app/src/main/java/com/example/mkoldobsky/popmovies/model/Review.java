package com.example.mkoldobsky.popmovies.model;

/**
 * Created by mkoldobsky on 15/9/15.
 */
public class Review {
    private final String id;
    private final String author;
    private final String content;
    private final String url;

    public Review(String id, String author, String content, String url) {

        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
