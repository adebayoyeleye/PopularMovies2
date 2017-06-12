package com.adebayoyeleye.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adebayo on 24/04/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private Movie[] results;
    private Context context;
    private final MoviesAdapterOnClickHandler mClickHandler;


    MoviesAdapter(Context c, MoviesAdapterOnClickHandler clickHandler) {
        context = c;
        mClickHandler = clickHandler;
    }


    /**
     * The interface that receives onClick messages.
     */
    interface MoviesAdapterOnClickHandler {
        void onClick(Movie movieClicked);
    }



    class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mMovieTitle;
        private ImageView mMoviePoster;

        MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);
            mMoviePoster = (ImageView)itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movieClicked = results[adapterPosition];

            mClickHandler.onClick(movieClicked);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Movie movie = results[position];
        String title = movie.getTitle();
        String posterPath = movie.getPosterPath();

        holder.mMovieTitle.setText(title);
//            Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+posterPath).into(holder.mMoviePoster);
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185/"+posterPath)
//                .placeholder(R.drawable.iv_loading)
//                .error(R.drawable.ic_action_name)
                .into(holder.mMoviePoster);

    }

    @Override
    public int getItemCount() {
        return results==null? 0: results.length;
    }

    void setResults(Movie[] results) {
        this.results = results;
        notifyDataSetChanged();
    }
}
