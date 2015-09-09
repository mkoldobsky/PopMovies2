package com.example.mkoldobsky.popmovies;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mkoldobsky.popmovies.model.Movie;
import com.example.mkoldobsky.popmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TrailerAdapter extends ArrayAdapter<Trailer> {
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<Trailer> mTrailers;

    public TrailerAdapter(Context c, int layoutResourceId, ArrayList<Trailer> trailers) {
        super(c, layoutResourceId, trailers);
        mContext = c;
        this.mLayoutResourceId = layoutResourceId;
        this.mTrailers = trailers;
    }


    public int getCount() {
        return mTrailers.size();
    }

    public Trailer getItem(int position) {
        return mTrailers.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.list_item_trailer_text_view);
            holder.imageView = (ImageView) row.findViewById(R.id.list_item_trailer_image_view);
            holder.imageView.setAdjustViewBounds(true);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Trailer trailer = mTrailers.get(position);
        holder.titleTextView.setText(Html.fromHtml(trailer.getName()));

//        Uri builtUri = Uri.parse("http://image.tmdb.org/t/p/w185" + trailer.getPosterPath()).buildUpon()
//                .build();
        Picasso.with(mContext)
                .load("@drawable/error")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .centerCrop()
                .fit()
                .into(holder.imageView);
        return row;
    }

    public void updateData(ArrayList<Trailer> newData) {
        if (newData != null){
            mTrailers.clear();
            mTrailers.addAll(newData);
            notifyDataSetChanged();
        }

    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

    //http://www.youtube.com/oembed?url=http://www.youtube.com/watch?v=AJ0sW7KOFhU&format=json
}