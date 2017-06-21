/*
 *This class is a modified version of the OpenWeatherJsonUtils class that was provided in the Sunshine app.
 */
package utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import com.example.android.popmovies.MovieData;
import com.example.android.popmovies.ReviewData;
import com.example.android.popmovies.TrailerData;

/**
 * Utility functions to handle theMovieDB JSON data.
 */
public final class TheMovieDBJsonUtils
{
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // The tag for all the movie information
    final static String OUTER_TAG = "results";

    // The tags for information about an individual movie
    final static String MD_POSTER_PATH = "poster_path";
    final static String MD_ADULT = "adult";
    final static String MD_OVERVIEW = "overview";
    final static String MD_RELEASE_DATE = "release_date";
    final static String MD_ID = "id";
    final static String MD_ORIGINAL_TITLE = "original_title";
    final static String MD_ORIGINAL_LANGUAGE = "original_language";
    final static String MD_TITLE = "title";
    final static String MD_BACKDROP_PATH = "backdrop_path";
    final static String MD_POPULARITY = "popularity";
    final static String MD_VOTE_COUNT = "vote_count";
    final static String MD_VIDEO = "video";
    final static String MD_VOTE_AVERAGE = "vote_average";

    public static ArrayList<ReviewData> getMovieReviewsFromJson(String MovieReviewJsonStr) throws JSONException
    {
        JSONObject reviewJson = new JSONObject(MovieReviewJsonStr);

        JSONArray reviewArray = reviewJson.getJSONArray(OUTER_TAG);

        ArrayList<ReviewData> parsedReviewData = new ArrayList<ReviewData>();

        Log.v(TAG, "Creating ReviewData objects for the " + reviewArray.length() + " reviews that were passed by the api");

        for (int i = 0; i < reviewArray.length(); i++)
        {
            JSONObject singleReviewJSON = reviewArray.getJSONObject(i);
            ReviewData singleReview = new ReviewData();

            singleReview.reviewerName = singleReviewJSON.getString("author");
            singleReview.reviewContent = singleReviewJSON.getString("content");

            Log.v(TAG, "Adding ReviewData for the review by " + singleReview.reviewerName);
            parsedReviewData.add(singleReview);
        }

        return parsedReviewData;
    }

    public static ArrayList<TrailerData> getMovieTrailersFromJson(String MovieTrailerJsonStr) throws JSONException
    {
        JSONObject trailerJson = new JSONObject(MovieTrailerJsonStr);

        JSONArray trailerArray = trailerJson.getJSONArray(OUTER_TAG);

        ArrayList<TrailerData> parsedTrailerData = new ArrayList<TrailerData>();

        Log.v(TAG, "Creating TrailerData objects for the " + trailerArray.length() + " Trailers that were passed by the api");

        for (int i = 0; i < trailerArray.length(); i++)
        {
            JSONObject singleTrailerJSON = trailerArray.getJSONObject(i);
            TrailerData singleTrailer = new TrailerData();

            singleTrailer.setTrailerKey(singleTrailerJSON.getString("key"));
            singleTrailer.trailerSite = singleTrailerJSON.getString("site");
            singleTrailer.trailerType = singleTrailerJSON.getString("type");
            singleTrailer.trailerName = singleTrailerJSON.getString("name");

            Log.v(TAG, "Created the TrailerData objects for the trailer " + singleTrailer.trailerName);

            if(singleTrailer.isTrailerSupported())
            {
                Log.v(TAG, "Added the supported movie trailer " + singleTrailer.trailerName);
                parsedTrailerData.add(singleTrailer);
            }
        }

        return parsedTrailerData;
    }

    /**
     * This method parses JSON from a web response and returns an array of MovieData with information
     * about each movies.
     *
     * @param MovieJsonStr JSON response from server
     *
     * @return Array list of MovieData describing each movie
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static ArrayList<MovieData> getMovieDataFromJson(String MovieJsonStr)
            throws JSONException
    {
        // The overall response as a JSONObject
        JSONObject movieJson = new JSONObject(MovieJsonStr);

        // The array of data about each movie
        JSONArray movieArray = movieJson.getJSONArray(OUTER_TAG);

        // stores the information about each movie
        ArrayList<MovieData> parsedMovieData = new ArrayList<>();
        Log.v(TAG, "Creating MovieData objects for the " + movieArray.length() + " movies that were passed by the api");
        // loop through all the JSON objects containing movie data
        for (int i = 0; i < movieArray.length(); i++)
        {

            /* Get the JSON object representing the movie data */
            JSONObject singleMovieJSON = movieArray.getJSONObject(i);
            MovieData singleMovie = new MovieData();

            // Retrieve and store all the data about a single movie
            singleMovie.poster_path = singleMovieJSON.getString(MD_POSTER_PATH);
            singleMovie.adult = singleMovieJSON.getString(MD_ADULT);
            singleMovie.overview = singleMovieJSON.getString(MD_OVERVIEW);
            String releaseData = singleMovieJSON.getString(MD_RELEASE_DATE);
            releaseData = releaseData.substring(0,releaseData.indexOf('-'));
            singleMovie.release_date = releaseData;
            singleMovie.id = singleMovieJSON.getString(MD_ID);
            singleMovie.original_title = singleMovieJSON.getString(MD_ORIGINAL_TITLE);
            singleMovie.original_language = singleMovieJSON.getString(MD_ORIGINAL_LANGUAGE);
            singleMovie.title = singleMovieJSON.getString(MD_TITLE);
            singleMovie.backdrop_path = singleMovieJSON.getString(MD_BACKDROP_PATH);
            singleMovie.popularity = singleMovieJSON.getString(MD_POPULARITY);
            singleMovie.vote_count = singleMovieJSON.getString(MD_VOTE_COUNT);
            singleMovie.video = singleMovieJSON.getString(MD_VIDEO);
            singleMovie.vote_average = singleMovieJSON.getString(MD_VOTE_AVERAGE) + "/10";

            Log.v(TAG, "Created the MovieData objects for the movie " + singleMovie.original_title);

            // save the single movies data in the list of all the movies
            parsedMovieData.add(singleMovie);
        }

        // Return the movie data
        return parsedMovieData;
    }
}