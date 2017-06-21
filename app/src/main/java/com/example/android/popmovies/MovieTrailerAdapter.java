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
 * This is used to display information about the movie's trailers
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder> {

    /**
     * Stores the current movie's trailer data
     */
    ArrayList<TrailerData> mTrailerData = new ArrayList<>();

    /**
     * Handles when the movie trailer is clicked on
     */
    private final MovieTrailerAdapterOnClickHandler mClickHandler;

    /**
     * when the view is created display the movies trailer information
     **/
    @Override
    public void onBindViewHolder(MovieTrailerAdapterViewHolder holder, int position)
    {
        TrailerData trailerData = mTrailerData.get(position);
        holder.mTrailerNameTextView.setText(trailerData.trailerName);
    }

    /**
     * Creates a MovieTrailerAdapter and instantiates the clickHandler
     *
     * @param clickHandler
     */
    public MovieTrailerAdapter(MovieTrailerAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieTrailerAdapterOnClickHandler
    {
        void onClick(TrailerData trailerData);
    }

    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieTrailerAdapterViewHolder(view);
    }

    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Displays the movie trailer name
         */
        public final TextView mTrailerNameTextView;

        public MovieTrailerAdapterViewHolder(View view)
        {
            super(view);
            // Get the text view and set the on click listener so we can determine when
            // the user has clicked on the movie trailer so we can display the trailer
            mTrailerNameTextView = (TextView) view.findViewById(R.id.trailerName);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            // pass the trailer information ot the on click
            int adapterPosition = getAdapterPosition();
            TrailerData singleTrailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(singleTrailer);
        }
    }

    /**
     * @return The total number of trailers we have data for
     */
    @Override
    public int getItemCount() {
        if (mTrailerData != null) {
            return mTrailerData.size();
        }
        return 0;
    }


    /**
     * Change the data that is saved in the adapter
     *
     * @param trailerData The trailer data to save in the adapter
     */
    public void setTrailerData(ArrayList<TrailerData> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }


}
