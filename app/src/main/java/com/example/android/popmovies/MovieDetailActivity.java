package com.example.android.popmovies;

import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.android.popmovies.data.FavoriteMoviesContract;
import com.example.android.popmovies.databinding.ActivityMovieDetailBinding;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import utilities.NetworkUtils;
import utilities.TheMovieDBJsonUtils;

/**
 * Displays detailed information about a movie such as the title, release date, and the movie poster
 * <p>
 * The idea of how to use multiple loaders in this activity is inspired by the stack overflow post:
 * https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity/20839825#20839825
 *
 * I referenced the link below to use an asyncqueryhandler to perform content provider calls asynchronously
 * http://codetheory.in/using-asyncqueryhandler-to-access-content-providers-asynchronously-in-android/
 */
public class MovieDetailActivity extends AppCompatActivity implements MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler {

    // used for manipulating various UI components with binding
    ActivityMovieDetailBinding mBinding;

    MovieReviewAdapter mMovieReviewAdapter;
    MovieTrailerAdapter mTrailerAdapter;

    int trailerLoaderID = 100;
    int reviewLoaderId = 200;

    int favoriteMovieHandlerID = 300;

    FavoritesQueryHandler favoritesQueryHandler;

    // all the data about the movie being displayed
    MovieData movieData;

    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    String movieIDKey;
    String apiToken;

    // Used to store if a movie is a favorite movie
    boolean isFavorite;

    /**
     * When a trailer is clicked launch an intent with the trailer's url
     *
     * @param trailerData
     */
    @Override
    public void onClick(TrailerData trailerData) {
        Intent trailerLauncher = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerData.getTrailerURL()));

        // if the trailer can be viewed launch the intent
        if (trailerLauncher.resolveActivity(getPackageManager()) != null) {
            startActivity(trailerLauncher);
        }
    }

    /**
     * check in the database if this is a favorite movie
     *
     * @return returns true if the movie is a favorite movie
     */
    private void isFavoriteMovie() {
        // Query the favorite movies database to determine if it contains movie data with the matching id
        Uri uri = FavoriteMoviesContract.FavoiteMovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieData.id).build();
        favoritesQueryHandler.startQuery(favoriteMovieHandlerID,null,uri, null, null, null, null);
        // set the favorite button to disabled while we attempt to validate if this is a favorite movie
        mBinding.favoriteButton.setEnabled(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // store the review data and trailer data so it can be retrieved if the screen is rotated
        outState.putParcelableArrayList("reviewData", mMovieReviewAdapter.mReviewData);
        outState.putParcelableArrayList("trailerData", mTrailerAdapter.mTrailerData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // set the binding to the correct layout
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        Intent startingIntent = getIntent();

        String movieDataKey = getString(R.string.movie_all_data_key);

        movieIDKey = getString(R.string.movie_id);
        apiToken = getString(R.string.apiToken);

        // Asynchronously makes requests to the content provider
        favoritesQueryHandler = new FavoritesQueryHandler(getContentResolver());

        // If the starting intent is not null retrieve the information passed in about the movie
        if (startingIntent != null) {

            // Check if the intent contains the various extras with the movie data, trailer and review data
            // and if they were passed in display them in the appropriate views or get new data from theMovieDB api.
            if (startingIntent.hasExtra(movieDataKey)) {
                ArrayList<MovieData> allPassedMovieData = startingIntent.getParcelableArrayListExtra(movieDataKey);
                if (allPassedMovieData.size() == 1) {
                    movieData = allPassedMovieData.get(0);


                    Log.v(TAG, "Displaying detailed information for the movie " + movieData.original_language);

                    // load the movie poster into the ImageView using the Picasso library or if a bitmap was
                    // provided use that instead
                    if (movieData.poster_data == null || movieData.poster_data.equals("")) {
                        Log.v(TAG, "Loading the movie poster from the Url: " + movieData.poster_path);
                        Picasso.with(getBaseContext()).load(movieData.getMoviePostURL()).into(mBinding.ivMoviePoster);
                    } else {
                        // This bitmap logic was inspired by the stack overflow post:
                        // https://stackoverflow.com/questions/8306623/get-bitmap-attached-to-imageview
                        Bitmap mapData = BitmapFactory.decodeByteArray(movieData.poster_data, 0, movieData.poster_data.length);
                        mBinding.ivMoviePoster.setImageBitmap(mapData);
                    }

                    // Display the movie data
                    mBinding.tvOriginalTitle.setText(movieData.title);
                    mBinding.tvReleaseDate.setText(movieData.release_date);
                    mBinding.tvPlotSynopsis.setText(movieData.overview);
                    mBinding.tvUserRating.setText(movieData.vote_average);

                }
            }

            // Get the recycler views that will display the trailers and the reviews
            RecyclerView mTrailersView = mBinding.rvTrailers;
            RecyclerView mReviewsView = mBinding.rvReviewss;

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            mTrailersView.setLayoutManager(layoutManager);

            LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
            mReviewsView.setLayoutManager(layoutManager2);

            // the child layouts size will not change in the RecyclerView
            mTrailersView.setHasFixedSize(false);
            mReviewsView.setHasFixedSize(false);

            mMovieReviewAdapter = new MovieReviewAdapter();
            mTrailerAdapter = new MovieTrailerAdapter(this);

            mReviewsView.setAdapter(mMovieReviewAdapter);
            mTrailersView.setAdapter(mTrailerAdapter);

            // If there is a saved instance state that contains previously loaded review and trailer data
            // use that data rather then re-obtaining the data.
            if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("reviewData") != null && savedInstanceState.getParcelableArrayList("trailerData") != null) {
                Log.v(TAG, "Using previously loaded movie data");

                ArrayList<ReviewData> reviewsSavedData = savedInstanceState.getParcelableArrayList("reviewData");
                mMovieReviewAdapter.setReviewData(reviewsSavedData);

                ArrayList<TrailerData> trailersSavedData = savedInstanceState.getParcelableArrayList("trailerData");
                mTrailerAdapter.setTrailerData(trailersSavedData);
            } else {
                Log.v(TAG, "No previously saved movie data, loading data from theMovieDB api");
                // if no previous data exists then retrieve new data

                Bundle movieDataBundle = new Bundle();
                movieDataBundle.putString(movieIDKey, movieData.id);
                getLoaderManager().initLoader(reviewLoaderId, movieDataBundle, reviewDataLoaderCallbacks);
                getLoaderManager().initLoader(trailerLoaderID, movieDataBundle, trailerDataLoaderCallbacks);

                // Set the favorite button to the proper state depending on if the movie is a favorite movie or not
                isFavoriteMovie();
            }
        }
    }

    private LoaderManager.LoaderCallbacks<ArrayList<TrailerData>> trailerDataLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<TrailerData>>() {

        @Override
        public Loader<ArrayList<TrailerData>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<TrailerData>>(getBaseContext())
            {
                @Override
                public ArrayList<TrailerData> loadInBackground() {
                    // get the url to retrieve movie data based on the selected filter
                    URL movieDBRequestUrl = NetworkUtils.buildTrailersUrl(args.getString(movieIDKey), apiToken);

                    try {

                        Log.v(TAG, "Retrieving trailer data using the Url: " + movieDBRequestUrl);
                        // get a response from theMovieDB
                        String MovieDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDBRequestUrl);

                        // parse the response into TrailerData objects that contain information about each movie trailer
                        ArrayList<TrailerData> allTrailerData = TheMovieDBJsonUtils
                                .getMovieTrailersFromJson(MovieDataResponse);
                        return allTrailerData;
                    } catch (Exception e) {
                        // if there was an issue print the stack trace to help determine what the issue is
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onStartLoading()
                {
                    // if we don't already have trailer data get the data
                   if(mTrailerAdapter.getItemCount() == 0)
                    {
                        forceLoad();
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<TrailerData>> loader, ArrayList<TrailerData> data) {
            Log.v(TAG, "Displaying the trailers ");

            if (data != null) {
                mTrailerAdapter.setTrailerData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<TrailerData>> loader) {

        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<ReviewData>> reviewDataLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<ReviewData>>() {
        @Override
        public Loader<ArrayList<ReviewData>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<ReviewData>>(getBaseContext()) {
                @Override
                public ArrayList<ReviewData> loadInBackground() {
                    // get the url to retrieve movie reviews data
                    URL movieDBRequestUrl = NetworkUtils.buildReviewUrl(args.getString(movieIDKey), apiToken);

                    try {

                        Log.v(TAG, "Retrieving movie review data using the Url: " + movieDBRequestUrl);
                        // get a response from theMovieDB
                        String MovieDataResponse = NetworkUtils.getResponseFromHttpUrl(movieDBRequestUrl);

                        // parse the response into ReviewData objects that contain information about each movie review
                        ArrayList<ReviewData> allReviewsData = TheMovieDBJsonUtils
                                .getMovieReviewsFromJson(MovieDataResponse);
                        return allReviewsData;
                    } catch (Exception e) {
                        // if there was an issue print the stack trace to help determine what the issue is
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onStartLoading()
                {
                    // If there isn't any review data then get data from the api
                    if(mMovieReviewAdapter.getItemCount() == 0)
                    {
                        forceLoad();
                    }
                }
            };
        }


        @Override
        public void onLoadFinished(Loader<ArrayList<ReviewData>> loader, ArrayList<ReviewData> data) {
            Log.v(TAG, "Displaying the reviews");
            if (data != null) {
                mMovieReviewAdapter.setReviewData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<ReviewData>> loader) {

        }
    };

    /** Insert a new favorite movie or remove it if  it was already a favorite movie
     *
     * @param view
     */
    public void favoriteMovie(View view)
    {
        // if the movie is not a favorite movie add it to the database otherwise it is already a favorite
        // so the user wants to remove it as a favorite
        if (!isFavorite)
        {
            // Insert new favorite movie data via the ContentResolver
            ContentValues contentValues = new ContentValues();

            // The logic to get the movie poster data as a bitmap to store into a database as a blob was inspired
            // by the stack overflow post: https://stackoverflow.com/a/9357943
            Bitmap bitmap = ((BitmapDrawable) mBinding.ivMoviePoster.getDrawable()).getBitmap();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

            byte[] imageData = outputStream.toByteArray();
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.v(TAG, "Error encountered while storing the movie poster for: " + movieData.original_title);
                e.printStackTrace();
            }

            // store all the movie data
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER_SIZE, imageData.length);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER_Data, imageData);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER, movieData.poster_path);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_ADULT, movieData.adult);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_OVERVIEW, movieData.overview);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_RELEASE_DATE, movieData.release_date);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_MOVIE_DB_ID, movieData.id);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_ORIGINAL_TITLE, movieData.title);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_BACKDROP, movieData.backdrop_path);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POPULARITY, movieData.popularity);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VOTE_COUNT, movieData.vote_count);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VIDEO, movieData.video);
            contentValues.put(FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VOTE_AVERAGE, movieData.vote_average);

            // Insert the movie data via the content resolver
            favoritesQueryHandler.startInsert(favoriteMovieHandlerID,null,FavoriteMoviesContract.FavoiteMovieEntry.CONTENT_URI, contentValues);
        }
        else
        {
            // Remove the favorite movie from the database by specifying the movie to delete
            Uri uri = FavoriteMoviesContract.FavoiteMovieEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieData.id).build();

            favoritesQueryHandler.startDelete(favoriteMovieHandlerID, null, uri, null, null);
        }
        // toggle the button so it matches the current state of if the movie is a favorite movie or not
        isFavoriteMovie();
    }

    /**
     *  Toggle the button so it matches the current state of if the movie is a favorite movie or not
     */
    private void setFavorite() {
        mBinding.favoriteButton.setEnabled(true);
        if (isFavorite)
        {
            mBinding.favoriteButton.setText(getString(R.string.favoritedString));
        }
        else
        {
            mBinding.favoriteButton.setText(getString(R.string.nonfavoritedString));
        }
    }

    // Asynchronously makes requests to the content provider
    class FavoritesQueryHandler extends AsyncQueryHandler {

        FavoritesQueryHandler(ContentResolver cr)
        {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            int count = cursor.getCount();
            cursor.close();

            // If more then 0 rows were returned then it is in the favorite movies database
            if (count > 0) {
                isFavorite = true;
                setFavorite();
            } else {
                isFavorite = false;
                setFavorite();
            }
        }
    }
}