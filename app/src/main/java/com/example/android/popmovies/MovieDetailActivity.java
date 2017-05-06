package com.example.android.popmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import utilities.NetworkUtils;

/**
 * Displays detailed information about a movie such as the title, release date, and the movie poster
 */
public class MovieDetailActivity extends AppCompatActivity
{
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // The text views that will display the movie data
    TextView mOriginalTitle;
    TextView mPlotSynopsis;
    TextView mUserRating;
    TextView mReleaseDate;

    /**
     * The image view that will display the movie's poster
     */
    ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent startingIntent = getIntent();

        // Retrieve the id's for the views we will display the data in
        mOriginalTitle = (TextView)findViewById(R.id.tv_original_title);
        mPlotSynopsis = (TextView)findViewById(R.id.tv_plot_synopsis);
        mUserRating = (TextView)findViewById(R.id.tv_user_rating);
        mReleaseDate = (TextView)findViewById(R.id.tv_release_date);
        mMoviePoster = (ImageView)findViewById(R.id.iv_movie_poster);

        // If the starting intent is not null retrieve the information passed in about the movie
        if(startingIntent != null)
        {
           // Get the keys for each of the data points that should be passed in
           String originalTitleKey  = getString(R.string.movie_original_title);
           String moviePostAddressKey = getString(R.string.movie_poster_address);
           String movieReleaseDateKey = getString(R.string.movie_release_date);
           String movieOverviewKey = getString(R.string.movie_overview_address);
           String movieUserRatingKey = getString(R.string.movie_rating_date);

           // Check if the intent contains the various extras with the movie data points
           // and if they were passed in display them in the appropriate views.

            if(startingIntent.hasExtra(originalTitleKey))
            {
                String originalTitle = startingIntent.getStringExtra(originalTitleKey);
                mOriginalTitle.setText(originalTitle);
                Log.v(TAG, "Displaying detailed information for the movie " + originalTitle);
            }

            if(startingIntent.hasExtra(moviePostAddressKey))
            {
                String moviePosterThumbnailAddress = startingIntent.getStringExtra(moviePostAddressKey);

                Log.v(TAG, "Loading the movie poster from the Url: " + mMoviePoster);
                // load the movie poster into the ImageView using the Picasso library
                Picasso.with(getBaseContext()).load(moviePosterThumbnailAddress).into(mMoviePoster);
            }

            if (startingIntent.hasExtra(movieReleaseDateKey))
            {
                String releaseDate = startingIntent.getStringExtra(movieReleaseDateKey);
                mReleaseDate.setText(releaseDate);
            }

            if(startingIntent.hasExtra(movieOverviewKey))
            {
                String movieOverview = startingIntent.getStringExtra(movieOverviewKey);
                mPlotSynopsis.setText(movieOverview);
            }

            if(startingIntent.hasExtra(movieUserRatingKey))
            {
                String movieUserRating = startingIntent.getStringExtra(movieUserRatingKey);
                mUserRating.setText(movieUserRating);
            }
        }
    }
}