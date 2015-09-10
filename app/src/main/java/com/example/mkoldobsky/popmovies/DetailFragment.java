package com.example.mkoldobsky.popmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mkoldobsky.popmovies.common.Constants;
import com.example.mkoldobsky.popmovies.common.Utility;
import com.example.mkoldobsky.popmovies.helper.MovieTrailerFactoryMethod;
import com.example.mkoldobsky.popmovies.model.Movie;
import com.example.mkoldobsky.popmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailFragment extends Fragment {

    private View mRootView;
    private Movie mMovie;
    ViewHolder mViewHolder;
    private boolean mError;
    private String mErrorMessage;
    TrailerAdapter mTrailerAdapter;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return mRootView;
    }

    public void setMovie(Movie movie){
        this.mMovie = movie;
        setMovieAdditionalInfo();

        createViewHolder();
        updateDetails();
        loadImages();
    }

    private void populateTrailers() {
        mTrailerAdapter = new TrailerAdapter(getActivity(), R.layout.list_item_trailer, mMovie.getTrailers());
        mViewHolder.trailerListView.setAdapter(mTrailerAdapter);
        mViewHolder.trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Trailer> trailers = mMovie.getTrailers();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+ trailers.get(position).getKey())));
            }
        });

    }

    private void setMovieAdditionalInfo() {
        if (Utility.isNetworkAvailable(getActivity())) {
            FetchMovieInfoTask movieInfoTask = new FetchMovieInfoTask();
            movieInfoTask.execute("trailers");
        } else {
            showErrorMessage(this.getString(R.string.network_error));
        }

    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.titleTextView = (TextView)mRootView.findViewById(R.id.movie_title_textview);
        mViewHolder.moviePosterImageView = (ImageView)mRootView.findViewById(R.id.movie_poster_imageview);
        mViewHolder.plotSynopsisTextView = (TextView)mRootView.findViewById(R.id.plot_synopsis_textview);
        mViewHolder.releaseDateTextView = (TextView)mRootView.findViewById(R.id.release_date_textview);
        mViewHolder.userRatingBar = (RatingBar)mRootView.findViewById(R.id.user_rating_bar);
        mViewHolder.trailerListView = (ListView)mRootView.findViewById(R.id.trailers_list_view);
    }

    private void updateDetails() {
        mViewHolder.titleTextView.setText(mMovie.getTitle());
        mViewHolder.plotSynopsisTextView.setText(this.getText(R.string.not_available));
        mViewHolder.releaseDateTextView.setText(this.getText(R.string.not_available));
        if (mMovie.getPlotSynopsis() != null && !mMovie.getPlotSynopsis().equals("null")) {
            mViewHolder.plotSynopsisTextView.setText(mMovie.getPlotSynopsis());
        }
        if (mMovie.getReleaseDate() != null && !mMovie.getReleaseDate().equals("null")) {
            mViewHolder.releaseDateTextView.setText(mMovie.getReleaseDate());
        }
        mViewHolder.userRatingBar.setRating(mMovie.getVoteAverage().floatValue() / 2);
    }

    private void loadImages() {
        final ImageView imageView = (ImageView)mRootView.findViewById(R.id.movie_poster);
        Uri builtUri = Uri.parse("http://image.tmdb.org/t/p/w185" + mMovie.getPosterPath()).buildUpon()
                .build();
        Picasso.with(getActivity()).load(builtUri.toString()).into(imageView);
        Picasso.with(getActivity()).load(builtUri.toString()).into(mViewHolder.moviePosterImageView);
    }


    public class FetchMovieInfoTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMovieInfoTask.class.getSimpleName();


        public FetchMovieInfoTask() {
        }


        private ArrayList<Trailer> getMovieTrailersFromJson(String jsonString)
                throws JSONException {

            final String MDB_RESULTS = "results";

            ArrayList<Trailer> results = new ArrayList<>();

            Log.v(LOG_TAG, jsonString);

            try {
                JSONObject trailersJson = new JSONObject(jsonString);
                JSONArray trailerArray = trailersJson.getJSONArray(MDB_RESULTS);

                for(int i = 0; i < trailerArray.length(); i++) {

                    JSONObject trailer = trailerArray.getJSONObject(i);

                    results.add(MovieTrailerFactoryMethod.create(trailer));
                }

                Log.d(LOG_TAG, "Complete. " + trailerArray.length() + " Trailers Fetched");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected Void doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String updateParam = params[0];

            mError = false;
            mErrorMessage = "";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            String jsonString;


            try {
                URL url = new URL(getTrailersUri(mMovie.getId()).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonString = buffer.toString();
                mMovie.setTrailers(getMovieTrailersFromJson(jsonString));
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                mError = true;
                mErrorMessage = e.getMessage();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                mError = true;
                mErrorMessage = e.getMessage();
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private Uri getTrailersUri(String id) {
            // https://api.themoviedb.org/3/movie/550/videos?api_key=xxxx
            final String FORECAST_BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+ id +"/videos?";
            final String API_KEY_PARAM = "api_key";

            return Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mError){
                showErrorMessage(mErrorMessage);
                return;
            }
            populateTrailers();
        }
    }

    private void showErrorMessage(String errorMessage) {

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), errorMessage, duration);
        toast.show();
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView moviePosterImageView;
        TextView plotSynopsisTextView;
        TextView releaseDateTextView;
        RatingBar userRatingBar;
        ListView trailerListView;
    }
}
