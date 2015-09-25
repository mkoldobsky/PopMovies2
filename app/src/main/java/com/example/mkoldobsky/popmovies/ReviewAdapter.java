package com.example.mkoldobsky.popmovies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mkoldobsky.popmovies.model.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private ArrayList<Review> mReviews;
    private int mRowLayout;

    public ReviewAdapter(ArrayList<Review> Reviews, int rowLayout) {
        this.mReviews = Reviews;
        this.mRowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Review review = mReviews.get(i);

        viewHolder.author.setText(review.getAuthor());
        viewHolder.review.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews == null ? 0 : mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView author;
        public TextView review;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.list_item_review_author_text_view);
            review = (TextView)itemView.findViewById(R.id.list_item_review_text_view);
        }
    }
}


