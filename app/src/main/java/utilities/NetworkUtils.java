/*
 *This class is a modified version of the NetworkUtils class that was provided in the Sunshine app.
 */
package utilities;

import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with theMovieDB api.
 */
public final class NetworkUtils
{
    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    // The base path for theMovieDB api
    private static final String MOVIES_DB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    // The key used to build the url to get the movie data
    private final static String API_TOKEN_PARAM = "api_key";

    /**
     * Builds the URL used to talk to the movieDB server based on which filter was selected.
     *
     * @param filterOption The filter that will be queried for.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrl(String filterOption,String apiToken)
    {
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
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}