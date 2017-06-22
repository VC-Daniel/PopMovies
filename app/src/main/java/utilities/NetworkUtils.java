/*
 *This class is a modified version of the NetworkUtils class that was provided in the Sunshine app.
 */
package utilities;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with theMovieDB api.
 * <p>
 * The logic for setting a timeout on the url connection was inspired by:
 * https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts/
 */
public final class NetworkUtils {
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // The base path for theMovieDB api
    private static final String MOVIES_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String reviewsPath = "reviews";
    private static final String trailersPath = "videos";

    // The key used to build the url to get the movie data
    private final static String API_TOKEN_PARAM = "api_key";

    // The connection and read time outs
    private static int connectionTimeout = 5000;
    private static int readTimeout = 10000;

    /**
     * Builds the URL used to talk to the movieDB server based on which filter was selected.
     *
     * @param filterOption The filter that will be queried for.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrl(String filterOption, String apiToken) {
        // build the uri with the base path, the supplied filter options and the api token
        Uri builtUri = Uri.parse(MOVIES_DB_BASE_URL).buildUpon()
                .appendPath(filterOption)
                .appendQueryParameter(API_TOKEN_PARAM, apiToken)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        // Return the path to use to retrieve the movie data
        return url;
    }

    /**
     * Builds the URL to retrieve a movie's trailers
     *
     * @param movieID  The movie's id.
     * @param apiToken
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildTrailersUrl(String movieID, String apiToken) {
        // build the uri with the base path, the selected movie's id and the api token
        Uri builtUri = Uri.parse(MOVIES_DB_BASE_URL).buildUpon()
                .appendPath(movieID).appendPath(trailersPath)
                .appendQueryParameter(API_TOKEN_PARAM, apiToken)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        // Return the path to use to retrieve the movie trailers
        return url;
    }

    /**
     * Builds the URL used to get the reviews of the movie
     *
     * @param movieID  The movie's id.
     * @param apiToken
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildReviewUrl(String movieID, String apiToken) {
        // build the uri with the base path, the supplied movie's id and the api token
        Uri builtUri = Uri.parse(MOVIES_DB_BASE_URL).buildUpon()
                .appendPath(movieID).appendPath(reviewsPath)
                .appendQueryParameter(API_TOKEN_PARAM, apiToken)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        // Return the path to use to retrieve the movie's reviews
        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        URLConnection connection = url.openConnection();

        // set the connection timeout and the read timeout
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        BufferedReader in = null;
        try {
            // get a stream to read data from
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }
}