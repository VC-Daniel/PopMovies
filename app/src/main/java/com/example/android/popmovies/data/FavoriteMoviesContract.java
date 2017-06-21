package com.example.android.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Daniel on 6/17/2017.
 *
 * Stores constants used to create and interact with the favorite movies database
 */

public class FavoriteMoviesContract implements BaseColumns
{
    public static final String AUTHORITY = "com.example.android.popmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "all_favorites";

    // Favorite Movie table and column names
    public static final class FavoiteMovieEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "FavoriteMovies";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

        public static final String COLUMN_POSTER_SIZE = "poster_size";
        public static final String COLUMN_POSTER_Data = "poster_data";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_ADULT = "adult";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_DB_ID = "moviedb_id";
        public static final String COLUMN_ORIGINAL_TITLE = "title";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_VOTE_AVERAGE = "vote_Average";
    }
}
