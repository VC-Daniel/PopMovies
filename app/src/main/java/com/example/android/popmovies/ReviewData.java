package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Daniel on 6/13/2017.
 *
 * Information about a single review including the author of the review and the contents fo the review
 */

public class ReviewData implements Parcelable
{
    public String reviewerName;
    public String reviewContent;

    public ReviewData(){}

    protected ReviewData(Parcel in) {
        reviewerName = in.readString();
        reviewContent = in.readString();
    }

    public static final Creator<ReviewData> CREATOR = new Creator<ReviewData>() {
        @Override
        public ReviewData createFromParcel(Parcel in) {
            return new ReviewData(in);
        }

        @Override
        public ReviewData[] newArray(int size) {
            return new ReviewData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reviewerName);
        dest.writeString(reviewContent);
    }
}
