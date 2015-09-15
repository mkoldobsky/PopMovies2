package com.example.mkoldobsky.popmovies.helper;

import com.example.mkoldobsky.popmovies.model.Movie;
import com.example.mkoldobsky.popmovies.model.Trailer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mkoldobsky on 9/9/15.
 */
public class MovieTrailerFactoryMethod {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SITE = "site";
    public static final String KEY = "key";

    public static Trailer create (JSONObject json) throws JSONException {
        String id = json.getString(ID);
        String name = json.getString(NAME);
        String site = json.getString(SITE);
        String key = json.getString(KEY);

        return new Trailer(id, name, site, key);
    }
}
