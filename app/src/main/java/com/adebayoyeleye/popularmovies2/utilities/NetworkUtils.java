package com.adebayoyeleye.popularmovies2.utilities;

import android.content.Context;
import android.net.Uri;


import android.support.v7.app.AppCompatActivity;

import com.adebayoyeleye.popularmovies2.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Adebayo on 23/04/2017.
 */

public final class NetworkUtils {
    private static final String TMDB_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    final static String API_KEY_PARAM = "api_key";

    public static URL buildUrl(String path, Context context) {
        String currentUrl = TMDB_BASE_URL+path;

        String apiKey = context.getResources().getString(R.string.api_key_string);
        Uri builtUri= Uri.parse(currentUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();



        URL url=null;
        try {
            url=new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setConnectTimeout(10000);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}
