package service;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.mkoldobsky.popmovies.data.MovieContract;
import com.example.mkoldobsky.popmovies.model.Movie;

import java.util.ArrayList;

/**
 * Created by mkoldobsky on 24/9/15.
 */
public class MovieService {

    private static final int INDEX_COLUMN_ID = 0;
    private static final int INDEX_COLUMN_TITLE = 1;
    private static final int INDEX_COLUMN_PATH = 2;
    private static final int INDEX_COLUMN_PLOT = 3;
    private static final int INDEX_COLUMN_VOTE = 4;
    private static final int INDEX_COLUMN_DATE = 5;
    private Context mContext;

    public MovieService(Context context){

        mContext = context;
    }

    public long addMovie(Movie movie) {
        long movieId;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{movie.getId()},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
        } else {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT, movie.getPlotSynopsis());
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE, movie.getVoteAverage());


            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();


        return movieId;
    }

    public long deleteMovie(Movie movie) {
        int rowsDeleted = 0;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                " = ?",
                new String[]{movie.getId()},
                null);

        if (movieCursor.moveToFirst()) {
            rowsDeleted = mContext.getContentResolver().delete(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MovieEntry._ID + " = ?",
                    new String[]{movie.getId()}

            );

        }
        return rowsDeleted;
    }

    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> result = new ArrayList<>();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_TITLE, MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE, MovieContract.MovieEntry.COLUMN_PLOT, MovieContract.MovieEntry.COLUMN_VOTE},
                null,
                null, null
        );

        while (movieCursor.moveToNext()){
            String id = movieCursor.getString(INDEX_COLUMN_ID);
            String title = movieCursor.getString(INDEX_COLUMN_TITLE);
            String path = movieCursor.getString(INDEX_COLUMN_PATH);
            String plot = movieCursor.getString(INDEX_COLUMN_PLOT);
            Double vote = movieCursor.getDouble(INDEX_COLUMN_VOTE);
            String date = movieCursor.getString(INDEX_COLUMN_DATE);
            Movie movie = new Movie(id, title, path, plot, vote, date);
            result.add(movie);

        }
        movieCursor.close();
        return result;
    }
}
