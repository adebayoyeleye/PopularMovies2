package com.adebayoyeleye.popularmovies2.data;

import android.provider.BaseColumns;

/**
 * Created by Adebayo on 11/06/2017.
 */

public class MoviesContract {
    private MoviesContract() {
    }
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_OVERVIEW = "overview";

    }

}
