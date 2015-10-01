package com.example.mkoldobsky.popmovies.model;

/**
 * Created by mkoldobsky on 9/9/15.
 */
public class Trailer {
    private String id;
    private String name;
    private String site;
    private String key;

    public Trailer(String id, String name, String site, String key){
        this.id = id;
        this.name = name;
        this.site = site;
        this.key = key;
    }
    public Trailer(String key, String name){

        this.key = key;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
