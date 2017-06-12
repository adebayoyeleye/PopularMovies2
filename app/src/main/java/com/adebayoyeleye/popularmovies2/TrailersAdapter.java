package com.adebayoyeleye.popularmovies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Adebayo on 10/06/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {

    private Trailer[] results;
    private Context context;
    private final TrailersAdapter.TrailersAdapterOnClickHandler mClickHandler;


    TrailersAdapter(Context c, TrailersAdapter.TrailersAdapterOnClickHandler clickHandler) {
        context = c;
        mClickHandler = clickHandler;
    }


    /**
     * The interface that receives onClick messages.
     */
    interface TrailersAdapterOnClickHandler {
        void onClick(Trailer trailerClicked);
    }



    class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTrailerName;

        TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerName = (TextView) itemView.findViewById(R.id.tv_trailers);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer trailerClicked = results[adapterPosition];

            mClickHandler.onClick(trailerClicked);
        }
    }

    @Override
    public TrailersAdapter.TrailersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailers_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new TrailersAdapter.TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.TrailersAdapterViewHolder holder, int position) {
        Trailer trailer = results[position];
        String name = trailer.getName();

        holder.mTrailerName.setText(name);
    }

    @Override
    public int getItemCount() {
        return results==null? 0: results.length;
    }

    void setResults(Trailer[] results) {
        this.results = results;
        notifyDataSetChanged();
    }
}

