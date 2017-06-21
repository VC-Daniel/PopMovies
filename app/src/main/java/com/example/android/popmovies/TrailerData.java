package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Daniel on 6/13/2017.
 *
 * Information about a single movie trailer including the name and url to view the trailer
 */

public class TrailerData implements Parcelable
{
    public String trailerSite;
    public String trailerType;
    private String trailerKey;
    public String trailerName;

    private static final String baseYouTubeLink = "https://www.youtube.com/watch?v=";
    private static final String[] supportedTrailerSite = new String[]{"YouTube"};

    public TrailerData(){}

    public  void setTrailerKey(String key)
    {
        trailerKey = key;
    }

    public String getTrailerURL()
    {
        return baseYouTubeLink + trailerKey;
    }

    public Boolean isTrailerSupported()
    {

        // if the trailer is from a supported site such as youtube then we can build the url to watch the trailer
        for (String site : supportedTrailerSite)
        {
            if(site.equals(trailerSite))
            {
             return true;
            }
        }

        return false;
    }

    protected TrailerData(Parcel in) {
        trailerSite = in.readString();
        trailerType = in.readString();
        trailerKey = in.readString();
        trailerName = in.readString();
    }

    public static final Creator<TrailerData> CREATOR = new Creator<TrailerData>() {
        @Override
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        @Override
        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trailerSite);
        dest.writeString(trailerType);
        dest.writeString(trailerKey);
        dest.writeString(trailerName);
    }
}