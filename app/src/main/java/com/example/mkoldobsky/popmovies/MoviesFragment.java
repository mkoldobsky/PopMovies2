package com.example.mkoldobsky.popmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.mkoldobsky.popmovies.common.Constants;
import com.example.mkoldobsky.popmovies.common.Utility;
import com.example.mkoldobsky.popmovies.helper.MovieFactoryMethod;
import com.example.mkoldobsky.popmovies.model.Movie;

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

import com.example.mkoldobsky.popmovies.service.MovieService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();

    private static final String MOVIES_KEY = "movies";
    MovieAdapter mMovieAdapter;
    ArrayList<Movie> mMovies;
    boolean mError;
    String mErrorMessage;
    MovieService mMovieService;
    String mSortOrder;

    OnMovieSelectedListener mCallback;


    public interface OnMovieSelectedListener{
        // Container Activity must implement this interface
        public void onMovieSelected(Movie movie);
    }


    public MoviesFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort_order, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        mMovieService = new MovieService(getActivity());

        setSortOrder();

        if (savedInstanceState != null)
        {
            mMovies = (ArrayList<Movie>)savedInstanceState.get(MOVIES_KEY);
        } else {
            mMovies = new ArrayList<>();
            updateMovies(mSortOrder);
        }

        mMovieAdapter = new MovieAdapter(this.getActivity(), R.layout.grid_item_movie, mMovies);


        setActivityTitle(getTitle(mSortOrder));
        GridView moviesGridView = (GridView)rootView.findViewById(R.id.moviesGridView);
        View empty = rootView.findViewById(R.id.empty);
        moviesGridView.setEmptyView(empty);
        moviesGridView.setAdapter(mMovieAdapter);
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = mMovies.get(position);
                selectedMovie.setFavorite(mMovieService.getFavorite(selectedMovie));
                Log.d(LOG_TAG, "favorite " + selectedMovie.getFavorite());

                mCallback.onMovieSelected(selectedMovie);

            }
        });

        return rootView;
    }

    private void setSortOrder() {
        mSortOrder = Utility.getPrefSortOrder(getActivity());

        if (mSortOrder == null){
            mSortOrder = Constants.MOST_POPULAR_SORT_ORDER;
            Utility.setPrefSortOrder(getActivity(), mSortOrder);
        }
    }

    private String getTitle(String sortOrder) {
        return sortOrder.equals(Constants.MOST_POPULAR_SORT_ORDER) ? getActivity().getString(R.string.action_most_popular) :
                sortOrder.equals(Constants.HIGHEST_RATED_SORT_ORDER) ? getActivity().getString(R.string.action_highest_rated) :
                        getActivity().getString(R.string.action_favorites);
    }

    @Override
    public void onResume() {
        super.onResume();

        setSortOrder();
        updateMovies(mSortOrder);
        setActivityTitle(getTitle(mSortOrder));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_KEY, (ArrayList<? extends Parcelable>) mMovies);
    }

    public void updateFavorites() {
        setSortOrder();
        if (mSortOrder.equals(Constants.FAVORITES)){
            updateMovies(mSortOrder);
        }

    }


    private void updateMovies(String sortOrder) {
        if (sortOrder.equals(Constants.FAVORITES)) {
            ArrayList<Movie> favorites = mMovieService.getMovies();
            mMovies.clear();
            if (favorites != null && mMovieAdapter != null) {
                mMovieAdapter.updateData(favorites);
            }


            return;
        }
        if (Utility.isNetworkAvailable(getActivity())) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute(sortOrder);
        } else {
            mMovies.clear();
            mMovieAdapter.updateData(mMovies);
            showErrorMessage(getContext().getString(R.string.network_error));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_most_popular) {
            updateMovies(Constants.MOST_POPULAR_SORT_ORDER);
            Utility.setPrefSortOrder(getActivity(), Constants.MOST_POPULAR_SORT_ORDER);
            setActivityTitle(getContext().getString(R.string.action_most_popular));
            return true;
        }

        if (id == R.id.action_highest_rated) {
            updateMovies(Constants.HIGHEST_RATED_SORT_ORDER);
            Utility.setPrefSortOrder(getActivity(), Constants.HIGHEST_RATED_SORT_ORDER);
            setActivityTitle(getContext().getString(R.string.action_highest_rated));
            return true;
        }

        if (id == R.id.action_favorites) {
            updateMovies(Constants.FAVORITES);
            Utility.setPrefSortOrder(getActivity(), Constants.FAVORITES);
            setActivityTitle(getContext().getString(R.string.action_favorites));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setActivityTitle(String title) {
        ((MainActivity)getActivity()).setActivityTitle(title);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private static final String MOST_POPULAR = "most_popular";
        private static final String MOST_POPULAR_VALUE = "popularity.desc";
        private static final String HIGHEST_RATED_VALUE = "vote_average.desc";
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        public FetchMoviesTask() {
        }


        private ArrayList<Movie> getMovieDataFromJson(String movieJsonString)
                throws JSONException {

            final String MDB_PAGE = "page";

            final String MDB_RESULTS = "results";

            ArrayList<Movie> results = new ArrayList<>();

            Log.v(LOG_TAG, movieJsonString);

            try {
                JSONObject moviesJson = new JSONObject(movieJsonString);
                JSONArray movieArray = moviesJson.getJSONArray(MDB_RESULTS);

                for(int i = 0; i < movieArray.length(); i++) {
                    JSONObject movieJson = movieArray.getJSONObject(i);

                    results.add(MovieFactoryMethod.create(movieJson));
                }


                Log.d(LOG_TAG, "FetchMoviesTask Complete. " + movieArray.length() + " Fetched");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return results;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            // Need highestRank or mostPopular
            if (params.length == 0) {
                return null;
            }
            String sortOrder = params[0];

            mError = false;
            mErrorMessage = "";

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonString;


            try {
                URL url = new URL(getUri(sortOrder).toString());

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
                moviesJsonString = buffer.toString();
                return getMovieDataFromJson(moviesJsonString);
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

        private Uri getUri(String sortOrder) {
            // https://api.themoviedb.org/3/discover/movie?api_key=xxxx&sort_by=popularity.desc
            //https://api.themoviedb.org/3/discover/movie?api_key=xxxx&sort_by=vote_average.desc
            final String MOVIE_BASE_URL =
                    "https://api.themoviedb.org/3/discover/movie?";
            final String API_KEY_PARAM = "api_key";
            final String SORT_BY_PARAM = "sort_by";
            final String SORT_BY_VALUE = sortOrder.equals(Constants.MOST_POPULAR_SORT_ORDER) ? MOST_POPULAR_VALUE : HIGHEST_RATED_VALUE;

            return Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Constants.API_KEY)
                    .appendQueryParameter(SORT_BY_PARAM, SORT_BY_VALUE)
                    .build();
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> result) {
            if (mError){
                showErrorMessage(mErrorMessage);
                return;
            }
            mMovies.clear();
            if (result != null) {
                mMovieAdapter.updateData(result);
            }
        }
    }

    private void showErrorMessage(String errorMessage) {

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), errorMessage, duration);
        toast.show();
    }

}
