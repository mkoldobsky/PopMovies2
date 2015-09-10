package com.example.mkoldobsky.popmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mkoldobsky.popmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{

    private ArrayList<Trailer> mTrailers;
    private int mRowLayout;
    private Context mContext;

    public TrailerAdapter(Context context, ArrayList<Trailer> trailers, int rowLayout) {
        this.mTrailers = trailers;
        this.mRowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Trailer trailer = mTrailers.get(i);
        viewHolder.name.setText(trailer.getName());
        Picasso.with(mContext)
                .load("@drawable/error")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .fit()
                .into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return mTrailers == null ? 0 : mTrailers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.list_item_trailer_text_view);
            image = (ImageView)itemView.findViewById(R.id.list_item_trailer_image_view);
        }

    }
}


//    //http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=AJ0sW7KOFhU&format=json


