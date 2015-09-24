package com.example.mkoldobsky.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mkoldobsky on 21/9/15.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.mkoldobsky.popmovies";
    public static final String PATH_MOVIE = "movie";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public static class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_RELEASE_DATE = "release";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
