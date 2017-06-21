// Logic for storing an image as a blob was inspired by: https://stackoverflow.com/a/32163951

package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 6/17/2017.
 *
 * Helps to interact with the favorite movies database. The favorite movies database
 * store all the data about a users favorite movies
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper
{

    // The name of the database
    private static final String DATABASE_NAME = "favoriteMoviesDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    // Constructor
    FavoriteMoviesDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // create the database with the columns in the FavoriteMoviesContract
        final String CREATE_TABLE = "CREATE TABLE "  + FavoriteMoviesContract.FavoiteMovieEntry.TABLE_NAME + " (" +
                FavoriteMoviesContract._ID                + " INTEGER PRIMARY KEY, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER_SIZE + " INTEGER NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER_Data + " BLOB, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POSTER + " BLOB NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_ADULT + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_MOVIE_DB_ID + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_BACKDROP + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_POPULARITY + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VOTE_COUNT + " TEXT NOT NULL, " +
                FavoriteMoviesContract.FavoiteMovieEntry.COLUMN_VIDEO + " TEXT NOT NULL"
                + ");";

      db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
