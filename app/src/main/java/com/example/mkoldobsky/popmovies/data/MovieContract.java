package com.example.mkoldobsky.popmovies.data;

import android.provider.BaseColumns;

/**
 * Created by mkoldobsky on 21/9/15.
 */
public class MovieContract {
    public class ReviewEntry implements BaseColumns{
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public class TrailerEntry implements BaseColumns{
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_MOVIE_ID = "movie_id";
    }

    public class MovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster";
        public static final String COLUMN_PLOT = "plot";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_RELEASE_DATE = "release";
    }
}
