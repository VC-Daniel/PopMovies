package com.example.android.popmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import utilities.NetworkUtils;
import utilities.TheMovieDBJsonUtils;

/**
 * Display a grid of movie posters. Either popular movies or top rated movie posters can be displayed.
 * Touch a movie poster to learn more about it.
 */
public class MoviesOverviewActivity extends AppCompatActivity implements MoviePosterAdapter.MoviePosterAdapterOnClickHandler
{
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Stores theMovieDB api token for use in this class
     */
    String apiToken;

    MoviePosterAdapter mMovieDataAdapter;
    RecyclerView mRecyclerView;
    ProgressBar mDataLoadingProgressBar;
    TextView mErrorTextView;

    /**
     * The filter used to retrieve data about popular movies
     */
    String POPULAR_FILTER;

    /**
     * The filter used to retrieve data about top rated movies
     */
    String TOP_RATED_FILTER;

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // store the movieData so it can be retrieved if the screen is rotated
        outState.putParcelableArrayList("movieData", mMovieDataAdapter.mMovieData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_overview);

        // get the api token
        apiToken = getString(R.string.apiToken);

        // get the values that should be used to build the api to retrieve data from theMovieDB api
        POPULAR_FILTER = getString(R.string.popular_movies_api);
        TOP_RATED_FILTER = getString(R.string.top_rated_api);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_movie_posters);
        mDataLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);

        // the child layouts size will not change in the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mMovieDataAdapter = new MoviePosterAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieDataAdapter);

        // if there is a saved instance state that contains previously loaded movieData
        // use that data  rather then re-obtaining the data.
        if(savedInstanceState != null && savedInstanceState.getParcelableArrayList("movieData") != null)
        {
            Log.v(TAG, "Using previously loaded movie data");
            ArrayList<MovieData> savedData = savedInstanceState.getParcelableArrayList("movieData");
            mMovieDataAdapter.setMovieData(savedData);
        }
        else
        {
            Log.v(TAG, "No previously saved movie data, loading data from theMovieDB api");
            // if no previous data exists then retrieve data on popular movies to populate the grid
            loadMovieData(POPULAR_FILTER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // show the different filter options in the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        // If the user has selected the popular movies or top rated filter then load retrieve
        // the movie data from theMovieDB
        if (id == R.id.action_popular)
        {
            Log.v(TAG, "Handling user click to retrieve data with the filter " + POPULAR_FILTER);
            loadMovieData(POPULAR_FILTER);
            return true;
        }
        else if (id == R.id.action_top_rated)
        {
            Log.v(TAG, "Handling user click to retrieve data with the filter " + TOP_RATED_FILTER);
            loadMovieData(TOP_RATED_FILTER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Retrieve movie data from theMovieDB with the supplied filter and display it in the
     * recyclerView as a grid. While the data is loading it will display a loading indicator
     * and if there is an error the user will be notified
     *
     * @param filter the type of movie to retrieve data for. For example popular or top_rated
     */
    private void loadMovieData(String filter)
    {
        // hide the error message text view while attempting to get new data
        mErrorTextView.setVisibility(View.INVISIBLE);

        // display the loading indicator while attempting to get movie data
        displayLoadingIndicator(true);

        // clear out the existing movie data
        mMovieDataAdapter.setMovieData(null);

        Log.v(TAG, "Create an async task to get the movie data from the api");
        // get movie data from theMovieDB using the chosen filter
        new FetchMovieData().execute(filter);
    }

    /**
     * Show a loading indicator or display the grid of movie posters
     *
     * @param isLoading if the loading indicator should be displayed and if the recyclerView should be hidden
     */
    private void displayLoadingIndicator(boolean isLoading)
    {
        if(isLoading)
        {
            mDataLoadingProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
        else
        {
            mDataLoadingProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * On clicking a movie poster you will be shown additional details about the movie
     *
     * @param movieData Information about the selected movie
     */
    @Override
    public void onClick(MovieData movieData)
    {
        // get the keys to store the movie data in the intent
        String originalTitleKey  = getString(R.string.movie_original_title);
        String moviePosterAddressKey = getString(R.string.movie_poster_address);
        String movieReleaseDateKey = getString(R.string.movie_release_date);
        String movieOverviewKey = getString(R.string.movie_overview_address);
        String movieUserRatingKey = getString(R.string.movie_rating_date);

        // the full address to the movie poster on theMovieDB
        String moviePosterAddress = movieData.getMoviePostURL();

        // create an intent to go to the movie detail activity
        Intent intent = new Intent(this,MovieDetailActivity.class);

        // store the movies data in the intent
        intent.putExtra(originalTitleKey,movieData.original_title);
        intent.putExtra(moviePosterAddressKey,moviePosterAddress);
        intent.putExtra(movieReleaseDateKey,movieData.release_date);
        intent.putExtra(movieOverviewKey,movieData.overview);
        intent.putExtra(movieUserRatingKey,movieData.vote_average);
        Log.v(TAG, "Handling user click on the movie poster for " + movieData.original_title);
        // launch the movie detail activity
        startActivity(intent);
    }


    /**
     * Retrieve movie data from theMovieDB in an async task
     */
    public class FetchMovieData extends AsyncTask<String, Void, ArrayList<MovieData>>
    {
        @Override
        protected void onPostExecute(ArrayList<MovieData> movieData)
        {
            Log.v(TAG, "Finished attempting to retrieve data from the api");
            // hide the loading indicator and display the grid of movie posters
            displayLoadingIndicator(false);

            // if movie  data was returned show the movie posters in the recycler view
            // otherwise display the error message
            if(movieData != null)
            {
                mMovieDataAdapter.setMovieData(movieData);
            }
            else
            {
                Log.v(TAG, "There was an issue while retrieving the movie data");
                mErrorTextView.setVisibility(View.VISIBLE);
            }
        }

        /**
         * @param filter
         * @return returns an array list of MovieData with information about all the movie data obtained from
         * theMovieDB
         */
        @Override
        protected ArrayList<MovieData> doInBackground(String[] filter)
        {
            // get the url to retrieve movie data based on the selected filter
            URL movieDBRequestUrl = NetworkUtils.buildUrl(filter[0], apiToken);

            try
            {

                Log.v(TAG, "Retrieving movie data using the Url: " + movieDBRequestUrl);
                // get a response from theMovieDB
                String MovieDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDBRequestUrl);

                // parse the response into MovieData objects that contain information about each movie
                ArrayList<MovieData> allMovieData = TheMovieDBJsonUtils
                        .getMovieDataFromJson(MovieDataResponse);
                return allMovieData;
            }
            catch (Exception e)
            {
                // if there was an issue print the stack trace to help determine what the issue is
                e.printStackTrace();
                return null;
            }
        }
    }
}