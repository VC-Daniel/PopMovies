package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel Sullivan on 5/2/2017.
 * Saves all the data relating to a single movie from theMovieDB
 *
 * The logic to write and retrieve the poster data in the parcelable is inspired by the stack overflow post
 * https://stackoverflow.com/questions/28135819/getting-nullpointerexception-attempt-to-get-length-of-null-array-in-parcelable
 */

public class MovieData implements Parcelable
{
    public int poster_size = 0;
    public byte[] poster_data;
    public String poster_path;
    public String adult;
    public String overview;
    public String release_date;
    public String id;
    public String original_title;
    public String original_language;
    public String title;
    public String backdrop_path;
    public String popularity;
    public String vote_count;
    public String video;
    public String vote_average;

    //the base path to retrieve a movie poster from
    private String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w342/";

    public MovieData(){}

    /**
     * @return the full address to the movie poster on theMovieDB
     */
    public String getMoviePostURL()
    {
        return  POSTER_BASE_PATH + poster_path;
    }

    //required creator for implementing parcelable
    public static final Creator<MovieData> CREATOR = new Creator<MovieData>()
    {
        @Override
        public MovieData createFromParcel(Parcel in)
        {
            return new MovieData(in);
        }

        @Override
        public MovieData[] newArray(int size)
        {
            return new MovieData[size];
        }
    };

    // convert a parcel into a MovieData object
    protected MovieData(Parcel in)
    {
        poster_size = in.readInt();

        // If there is a poster stored retrieve the poster data
        if(poster_size>0)
        {
            poster_data = new byte[poster_size];
            in.readByteArray(poster_data);
        }

        poster_path = in.readString();
        adult = in.readString();
        overview = in.readString();
        release_date = in.readString();
        id = in.readString();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readString();
        vote_count = in.readString();
        video = in.readString();
        vote_average = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write a MovieData object to a parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(poster_size);

        // if there is a poster store the poster
        if(poster_size>0)
        {
            dest.writeByteArray(poster_data);
        }
        dest.writeString(poster_path);
        dest.writeString(adult);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeString(popularity);
        dest.writeString(vote_count);
        dest.writeString(video);
        dest.writeString(vote_average);
    }
}