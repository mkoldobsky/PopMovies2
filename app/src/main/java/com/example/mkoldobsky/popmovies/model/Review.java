package com.example.mkoldobsky.popmovies.model;

/**
 * Created by mkoldobsky on 15/9/15.
 */
public class Review {
    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String id, String author, String content, String url) {

        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public Review(String author, String content){

        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
