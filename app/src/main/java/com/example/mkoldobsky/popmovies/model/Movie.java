package com.example.mkoldobsky.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable{
    private String id;
    private String title;
    private String posterPath;
    private String plotSynopsis;
    private Double voteAverage;
    private String releaseDate;
    private ArrayList<Trailer> trailers = new ArrayList<>();

    public Movie(String id, String title, String posterPath, String plot, Double vote, String date){
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.plotSynopsis = plot;
        this.voteAverage = vote;
        this.releaseDate = date;
    }

    public Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterPath = in.readString();
        plotSynopsis = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(plotSynopsis);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public ArrayList<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
    }

    public String getId() {
        return id;
    }

    public void addTrailers(ArrayList<Trailer> movieTrailersFromJson) {
        this.trailers.addAll(movieTrailersFromJson);
    }
}
