package com.adebayoyeleye.popularmovies2.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.adebayoyeleye.popularmovies2.*;


/**
 * Created by Adebayo on 24/04/2017.
 */

public final class ObjectJsonUtils {

    public static final String MOVIE_REVIEW_RESULTS = "results";
    public static final String TRAILER_RESULTS = "youtube";

    public static JSONArray jsonStringToArray (String jsonString, String arrayName) throws JSONException {
        JSONObject object = new JSONObject(jsonString);
        JSONArray jsonArrayResults = object.getJSONArray(arrayName);
        return jsonArrayResults;
    }



    public static Movie[] setMovieResults(String JSONString) throws JSONException {
        JSONArray jsonArrayResults = jsonStringToArray(JSONString, MOVIE_REVIEW_RESULTS);
        int jsonArrayResultSize = jsonArrayResults.length();
        JSONObject jsonMovie;
        Movie[] results = new Movie[jsonArrayResultSize];
        for (int i=0; i<jsonArrayResultSize; i++){
            jsonMovie = jsonArrayResults.getJSONObject(i);
            results[i]= new Movie(jsonMovie.getInt("id"),
                    jsonMovie.getString("original_title"),
                    jsonMovie.getString("poster_path"),
                    jsonMovie.getString("release_date"),
                    Float.parseFloat(String.valueOf(jsonMovie.get("vote_average"))),
                    jsonMovie.getString("overview"));
        }
        return results;

    }
    public static Trailer[] setTrailerResults(String JSONString) throws JSONException {
        JSONArray jsonArrayResults = jsonStringToArray(JSONString, TRAILER_RESULTS);
        int jsonArrayResultSize = jsonArrayResults.length();
        JSONObject jsonVideo;
        Trailer[] results = new Trailer[jsonArrayResultSize];
        for (int i=0; i<jsonArrayResultSize; i++){
            jsonVideo = jsonArrayResults.getJSONObject(i);
            results[i]= new Trailer(null,
                    jsonVideo.getString("source"),
                    jsonVideo.getString("name"));
        }
        return results;

    }
    public static Review[] setReviewResults(String JSONString) throws JSONException {
        JSONArray jsonArrayResults = jsonStringToArray(JSONString, MOVIE_REVIEW_RESULTS);
        int jsonArrayResultSize = jsonArrayResults.length();
        JSONObject jsonReview;
        Review[] results = new Review[jsonArrayResultSize];
        for (int i=0; i<jsonArrayResultSize; i++){
            jsonReview = jsonArrayResults.getJSONObject(i);
            results[i]= new Review(jsonReview.getString("id"),
                    jsonReview.getString("author"),
                    jsonReview.getString("content"),
                    jsonReview.getString("url"));
        }
        return results;

    }
}
