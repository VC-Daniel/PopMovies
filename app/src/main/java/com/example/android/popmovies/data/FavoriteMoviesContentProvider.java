package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.example.android.popmovies.data.FavoriteMoviesContract.FavoiteMovieEntry.TABLE_NAME;

/**
 * Created by Daniel on 6/17/2017.
 *
 * Facilitates interacting with the SQLite database that stores information about the users favorite movies
 *
 * This class is loosely based off of the swipe to delete lesson (T09.07)
 */

public class FavoriteMoviesContentProvider extends ContentProvider
{
    // Store the class name for logging
    private static final String TAG = FavoriteMoviesContentProvider.class.getSimpleName();

    // Used to get all the favorite movies in the table
    public static final int FAVORITES = 100;

    // Used  to get a specific favorite movie based on the id
    public static final int FAVORITE_WITH_ID = 101;

    // Used to determine how to respond to the supplied Uri
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     Initialize a new matcher object without any matches,
     then use .addURI(String authority, String path, int match) to add matches
     for all the favorites or for a specific favorite movie identified by it's id
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //add matches for the favorite directory and a single favorite by ID.
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_FAVORITES +"/#", FAVORITE_WITH_ID);
        return uriMatcher;
    }

    // Helps the class to interact with the database
    private FavoriteMoviesDbHelper mFavoriteMoviesDbHelper;

    @Override
    public boolean onCreate()
    {

        Context context = getContext();
        mFavoriteMoviesDbHelper = new FavoriteMoviesDbHelper(context);
        return true;
    }

    // Performs queries on the database to get all the favorites or a specific favorite based on the id
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {

        Log.v(TAG, "Get a readable database to query");
        // Get a readable database since we are only performing a query we don't need a writable database
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getReadableDatabase();

        // match the  uri to determine what query should be performed
        int match = sUriMatcher.match(uri);

        // Used to return the results of the query
        Cursor resultsCursor;

        // perform the requested query
        switch (match) {
            // Query for the entire favorites directory
            case FAVORITES:

                Log.v(TAG, "Retrieving all favorites based on the supplied options");
                resultsCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Query for getting the specific favorite movie that matches the supplied id if there is one
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                Log.v(TAG, "Retrieving the movie with id=" + id);
                resultsCursor =  db.query(TABLE_NAME,
                        projection,
                        "moviedb_id=?",
                        new String[] {id},
                        null,
                        null,
                        null);
                break;
            // Throw an exception if the uri did match a known query
            default:
                Log.v(TAG, "No matching query operation was found");
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        resultsCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return resultsCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Inserts new favorites into the directory
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)
    {
        Log.v(TAG, "Get a readable database to insert a new favorite movie");
        // Get access to the favorites database that we can write new data to
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        // Used to point to the new row
        Uri returnUri;

        switch (match)
        {
            // Insert data about a new favorite movie
            case FAVORITES:
                long id = db.insert(TABLE_NAME, null, values);
                Log.v(TAG, "Inserting a new favorite movie with the id " + id);

                // if a new row was inserted successfully create a uri that points to the row
                // otherwise throw an exception
                if ( id > 0 )
                {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesContract.FavoiteMovieEntry.CONTENT_URI, id);
                }
                else
                {
                    Log.v(TAG, "Error occurred while inserting a new favorite movie");
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Throw an exception if the uri did match a known insert operation
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    // Remove a favorite movie from the database
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs)
    {

        Log.v(TAG, "Get a readable database to delete a favorite movie");
        // Get access to the writable database
        final SQLiteDatabase db = mFavoriteMoviesDbHelper.getWritableDatabase();

        // Identify what the uri specifies to delete
        int match = sUriMatcher.match(uri);

        // stores the number of rows deleted
        int count;

        switch (match)
        {
            // delete a specific favorite movie based on the supplied id
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                Log.v(TAG, "Deleting the favorite movie with the id " + id);
                // Insert new values into the database
                // Inserting values into tasks table
                 count = db.delete(TABLE_NAME,"moviedb_id=?", new String[]{id});

                break;
            // Throw an exception if the uri did match a known delete operation
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // If more then one record has been deleted send a notification based on the supplied uri
        if (count != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows that where delted
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        return 0;
    }
}
