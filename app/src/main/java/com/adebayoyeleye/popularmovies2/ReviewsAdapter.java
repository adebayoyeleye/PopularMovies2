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

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private Review[] results;
    private Context context;
    private final ReviewsAdapter.ReviewsAdapterOnClickHandler mClickHandler;


    ReviewsAdapter(Context c, ReviewsAdapter.ReviewsAdapterOnClickHandler clickHandler) {
        context = c;
        mClickHandler = clickHandler;
    }


    /**
     * The interface that receives onClick messages.
     */
    interface ReviewsAdapterOnClickHandler {
        void onClick(Review reviewClicked);
    }



    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mReviewItem;

        ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewItem = (TextView) itemView.findViewById(R.id.tv_reviews);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Review reviewClicked = results[adapterPosition];

            mClickHandler.onClick(reviewClicked);
        }
    }

    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.reviews_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsAdapterViewHolder holder, int position) {
        Review review = results[position];
        String name = review.getAuthor() + "\n\n" + review.getContent();

        holder.mReviewItem.setText(name);
    }

    @Override
    public int getItemCount() {
        return results==null? 0: results.length;
    }

    void setResults(Review[] results) {
        this.results = results;
        notifyDataSetChanged();
    }
}

