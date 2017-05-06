package com.example.android.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by Daniel on 5/2/2017. This is a modified version of ForecastAdapter from the sunshine app.
 * This is used to display the movie posters in a grid by a recyclerView
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.MoviePosterAdapterViewHolder>
{

    /**
     * Stores the current movie data
     */
    ArrayList<MovieData> mMovieData = new ArrayList<>();

    /**
     * Handles when the movie poster is clicked on
     */
    private final MoviePosterAdapterOnClickHandler mClickHandler;

    /**
     * when the view is created display the movies poster
     **/
    @Override
    public void onBindViewHolder(MoviePosterAdapterViewHolder holder, int position)
    {
        MovieData movieData = mMovieData.get(position);
        Context context = holder.itemView.getContext();

        // populate the image view with the movie poster
        Picasso.with(context).load(movieData.getMoviePostURL()).into(holder.mMovieImageView);
    }

    /**
     * Creates a MoviePosterAdapter and instantiates the clickHandler
     *
     * @param clickHandler
     */
    public MoviePosterAdapter(MoviePosterAdapterOnClickHandler clickHandler)
    {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MoviePosterAdapterOnClickHandler
    {
        void onClick(MovieData movieData);
    }

    @Override
    public MoviePosterAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_poster_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviePosterAdapterViewHolder(view);
    }

    public class MoviePosterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        /**
         * Displays the movie poster
         */
        public final ImageView mMovieImageView;

        public MoviePosterAdapterViewHolder(View view)
        {
            super(view);
            // Get the image view and set the on click listener so we can determine when
            // the user has clicked on the movie poster
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_main_movie_poster);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v)
        {
            // pass the movie data that corresponds to the clicked movie
            int adapterPosition = getAdapterPosition();
            MovieData singleMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(singleMovie);
        }
    }

    /**
     * @return The total number of movies we have data for
     */
    @Override
    public int getItemCount()
    {
        if(mMovieData != null)
        {
            return mMovieData.size();
        }
        return  0;
    }


    /**
     * Change the data that is saved in the adapter
     *
     * @param movieData The movie data to save in the adapter
     */
    public void setMovieData(ArrayList<MovieData> movieData)
    {
        mMovieData = movieData;
        notifyDataSetChanged();
    }
}