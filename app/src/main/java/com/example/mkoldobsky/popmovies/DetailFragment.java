package com.example.mkoldobsky.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mkoldobsky.popmovies.common.Constants;
import com.example.mkoldobsky.popmovies.common.Utility;
import com.example.mkoldobsky.popmovies.helper.MovieReviewFactoryMethod;
import com.example.mkoldobsky.popmovies.helper.MovieTrailerFactoryMethod;
import com.example.mkoldobsky.popmovies.model.Movie;
import com.example.mkoldobsky.popmovies.model.Review;
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

import service.MovieService;

public class DetailFragment extends Fragment {

    private static final String MOVIE_SHARE_HASHTAG = " #PopMovies";
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    private View mRootView;
    private Movie mMovie;
    ViewHolder mViewHolder;
    private boolean mError;
    private String mErrorMessage;
    TrailerAdapter mTrailerAdapter;
    ReviewAdapter mReviewAdapter;
    FloatingActionButton mFab;
    MovieService mMovieService;
    ShareActionProvider mShareActionProvider;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieService = new MovieService(getActivity());
        mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mFab = (FloatingActionButton) mRootView.findViewById(R.id.favorite_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMovie.isFavorite()) {
                    mFab.setImageResource(R.drawable.ic_add_favorite);
                    mMovie.setFavorite(false);
                    mMovieService.deleteMovie(mMovie);
                } else {
                    mFab.setImageResource(R.drawable.ic_remove_favorite);
                    mMovie.setFavorite(true);
                    mMovieService.addMovie(mMovie);
                }
            }
        });

        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setMovie(Movie movie){
        this.mMovie = movie;

        createViewHolder();

        initializeFavoriteIcon();
        initializeTrailers();
        initializeReviews();

        setMovieAdditionalInfo();

        mViewHolder.trailersRecyclerView.setAdapter(mTrailerAdapter);
        mViewHolder.reviewsRecyclerView.setAdapter(mReviewAdapter);


        updateDetails();
        loadImages();
    }

    private void initializeFavoriteIcon() {
        if (mMovie.isFavorite()) {
            mFab.setImageResource(R.drawable.ic_remove_favorite);
        } else {
            mFab.setImageResource(R.drawable.ic_add_favorite);
        }

        }

    private void initializeTrailers() {
        mTrailerAdapter = new TrailerAdapter(getActivity(), mMovie.getTrailers(), R.layout.list_item_trailer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mViewHolder.trailersRecyclerView = (RecyclerView)mRootView.findViewById(R.id.trailers_recycler_view);

        mViewHolder.trailersRecyclerView.setLayoutManager(linearLayoutManager);
        mViewHolder.trailersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mViewHolder.trailersRecyclerView.setHasFixedSize(true);
    }

    private void initializeReviews() {
        mReviewAdapter = new ReviewAdapter(mMovie.getReviews(), R.layout.list_item_review);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mViewHolder.reviewsRecyclerView = (RecyclerView)mRootView.findViewById(R.id.reviews_recycler_view);

        mViewHolder.reviewsRecyclerView.setLayoutManager(linearLayoutManager);
        mViewHolder.reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mViewHolder.reviewsRecyclerView.setHasFixedSize(true);
    }

    private void populateTrailers() {
        mTrailerAdapter.notifyDataSetChanged();
        mReviewAdapter.notifyDataSetChanged();
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
        mViewHolder.trailersRecyclerView = (RecyclerView)mRootView.findViewById(R.id.trailers_recycler_view);
        mViewHolder.reviewsRecyclerView = (RecyclerView)mRootView.findViewById(R.id.reviews_recycler_view);
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

        private ArrayList<Review> getMovieReviewsFromJson(String jsonString)
                throws JSONException {

            final String MDB_RESULTS = "results";

            ArrayList<Review> results = new ArrayList<>();

            Log.v(LOG_TAG, jsonString);

            try {
                JSONObject reviewsJson = new JSONObject(jsonString);
                JSONArray reviewArray = reviewsJson.getJSONArray(MDB_RESULTS);

                for(int i = 0; i < reviewArray.length(); i++) {

                    JSONObject review = reviewArray.getJSONObject(i);

                    results.add(MovieReviewFactoryMethod.create(review));
                }

                Log.d(LOG_TAG, "Complete. " + reviewArray.length() + " Reviews Fetched");

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

            mError = false;
            mErrorMessage = "";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (getTrailers(urlConnection, reader)) return null;
            if (getReviews(urlConnection, reader)) return null;
            return null;
        }

        private boolean getTrailers(HttpURLConnection urlConnection, BufferedReader reader) {
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
                    return true;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return true;
                }
                jsonString = buffer.toString();
                mMovie.addTrailers(getMovieTrailersFromJson(jsonString));
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
            return false;
        }

        private boolean getReviews(HttpURLConnection urlConnection, BufferedReader reader) {
            String jsonString;
            try {
                URL url = new URL(getReviewsUri(mMovie.getId()).toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return true;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return true;
                }
                jsonString = buffer.toString();
                mMovie.addReviews(getMovieReviewsFromJson(jsonString));
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
            return false;
        }

        private Uri getTrailersUri(String id) {
            // https://api.themoviedb.org/3/movie/550/videos?api_key=xxxx
            final String BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+ id +"/videos?";
            final String API_KEY_PARAM = "api_key";

            return Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .build();
        }

        private Uri getReviewsUri(String id) {
            // https://api.themoviedb.org/3/movie/550/videos?api_key=xxxx
            final String BASE_URL =
                    "https://api.themoviedb.org/3/movie/"+ id +"/reviews?";
            final String API_KEY_PARAM = "api_key";

            return Uri.parse(BASE_URL).buildUpon()
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

    private Intent createShareTrailerIntent() {
        Trailer firstTrailer = mMovie.trailersSize() > 0 ?mMovie.getTrailers().get(0) : null;
        String textToShare = firstTrailer != null ? getTrailerLink(firstTrailer) : "ups!(no trailer)";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Watch the " + mMovie.getTitle() + "'s trailer " + textToShare + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }

    private String getTrailerLink(Trailer trailer){
        return Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()).toString();
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
        RecyclerView trailersRecyclerView;
        RecyclerView reviewsRecyclerView;
    }
}
