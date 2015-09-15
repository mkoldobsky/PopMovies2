package com.example.mkoldobsky.popmovies.helper;

import com.example.mkoldobsky.popmovies.model.Review;
import com.example.mkoldobsky.popmovies.model.Trailer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mkoldobsky on 9/9/15.
 */
public class MovieReviewFactoryMethod {
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String URL = "url";

    public static Review create (JSONObject json) throws JSONException {
        String id = json.getString(ID);
        String author = json.getString(AUTHOR);
        String content = json.getString(CONTENT);
        String url = json.getString(URL);

        return new Review(id, author, content, url);
    }
}
