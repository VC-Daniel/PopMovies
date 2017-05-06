/*
 *This class is a modified version of the OpenWeatherJsonUtils class that was provided in the Sunshine app.
 */
package utilities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.example.android.popmovies.MovieData;

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
    final static String MD_GENRE_ID = "genre_ids";
    final static String MD_ID = "id";
    final static String MD_ORIGINAL_TITLE = "original_title";
    final static String MD_ORIGINAL_LANGUAGE = "original_language";
    final static String MD_TITLE = "title";
    final static String MD_BACKDROP_PATH = "backdrop_path";
    final static String MD_POPULARITY = "popularity";
    final static String MD_VOTE_COUNT = "vote_count";
    final static String MD_VIDEO = "video";
    final static String MD_VOTE_AVERAGE = "vote_average";

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
            singleMovie.release_date = singleMovieJSON.getString(MD_RELEASE_DATE);
            singleMovie.id = singleMovieJSON.getString(MD_ID);
            singleMovie.original_title = singleMovieJSON.getString(MD_ORIGINAL_TITLE);
            singleMovie.original_language = singleMovieJSON.getString(MD_ORIGINAL_LANGUAGE);
            singleMovie.title = singleMovieJSON.getString(MD_TITLE);
            singleMovie.backdrop_path = singleMovieJSON.getString(MD_BACKDROP_PATH);
            singleMovie.popularity = singleMovieJSON.getString(MD_POPULARITY);
            singleMovie.vote_count = singleMovieJSON.getString(MD_VOTE_COUNT);
            singleMovie.video = singleMovieJSON.getString(MD_VIDEO);
            singleMovie.vote_average = singleMovieJSON.getString(MD_VOTE_AVERAGE);

            // Get the genres the movie is in and store them
            JSONArray JSON_Genres = singleMovieJSON.getJSONArray(MD_GENRE_ID);
            if(JSON_Genres != null && JSON_Genres.length() > 0)
            {
                singleMovie.genre_ids = new String[JSON_Genres.length()];

                for (int j = 0; j < JSON_Genres.length(); j++)
                {
                    singleMovie.genre_ids[j] = new String();
                    singleMovie.genre_ids[j] = JSON_Genres.get(j).toString();
                }
            }

            Log.v(TAG, "Created the MovieData objects for the movie " + singleMovie.original_title);

            // save the single movies data in the list of all the movies
            parsedMovieData.add(singleMovie);
        }

        // Return the movie data
        return parsedMovieData;
    }
}