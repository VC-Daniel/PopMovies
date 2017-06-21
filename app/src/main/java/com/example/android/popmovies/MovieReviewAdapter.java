package com.example.android.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Daniel on 6/13/2017.
 *
 * This is used to display movie reviews in a RecyclerView
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder>
{

    /**
     * Stores the current review data
     */
    ArrayList<ReviewData> mReviewData = new ArrayList<>();

    /**
     * when the view is created display the movie review
     **/
    @Override
    public void onBindViewHolder(MovieReviewAdapterViewHolder holder, int position)
    {
        ReviewData reviewData = mReviewData.get(position);
        holder.mAuthorTextView.setText(reviewData.reviewerName);
        holder.mReviewTextView.setText(reviewData.reviewContent);
    }


    public MovieReviewAdapter() {}

    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();

        // set the layout of the view to the correct layout
        int layoutIdForListItem = R.layout.movie_review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieReviewAdapterViewHolder(view);
    }

    /**
     * Displays the reviewer and the review
     */
    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder
    {
        public final TextView mAuthorTextView;
        public final TextView mReviewTextView;

        public MovieReviewAdapterViewHolder(View view)
        {
            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.reviewAuthor);
            mReviewTextView = (TextView) view.findViewById(R.id.reviewContent);
        }
    }

    /**
     * @return The total number of movies we have data for
     */
    @Override
    public int getItemCount()
    {
        if(mReviewData != null)
        {
            return mReviewData.size();
        }
        return  0;
    }

    /**
     * Change the data that is saved in the adapter
     *
     * @param reviewData The review data to save in the adapter
     */
    public void setReviewData(ArrayList<ReviewData> reviewData)
    {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }
}