package com.adebayoyeleye.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adebayoyeleye.popularmovies2.data.MoviesContract;
import com.adebayoyeleye.popularmovies2.data.MoviesDbHelper;
import com.adebayoyeleye.popularmovies2.utilities.ObjectJsonUtils;
import com.adebayoyeleye.popularmovies2.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private SQLiteDatabase mDb;

    private String sortBy = "popular";
    private RecyclerView mRecyclerView;
    private MoviesAdapter mMoviesAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    Movie[] moviesResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMoviesAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(Movie.MOVIE_EXTRA)) {
                Movie[] moviesResult = (Movie[]) savedInstanceState
                        .getParcelableArray(Movie.MOVIE_EXTRA);
                mMoviesAdapter.setResults(moviesResult);
                showMoviesDataView();
            } else {
                loadMoviesData();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(Movie.MOVIE_EXTRA, moviesResult);
    }

    private void loadMoviesData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        new AClass().execute(NetworkUtils.buildUrl(sortBy, this));

    }


    @Override
    public void onClick(Movie movieDetails) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Movie.MOVIE_EXTRA, movieDetails);
        startActivity(intentToStartDetailActivity);
    }




    private void showMoviesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_sortby_favourites||
                (id == R.id.action_refresh && sortBy == getResources().getString(R.string.menu_sort_by_3))) {
            sortBy = getResources().getString(R.string.menu_sort_by_3);
            moviesResult = getFavoriteMovies();
            mMoviesAdapter.setResults(moviesResult);
            return super.onOptionsItemSelected(item);
        }


            if (id == R.id.menu_sortby_popularity) {
            sortBy = getResources().getString(R.string.sort_by_popularity);
        }else if (id==R.id.menu_sortby_ratings){
            sortBy = getResources().getString(R.string.sort_by_rating);
        } else if (id==R.id.action_refresh){
            // just refresh the page
        }

        mMoviesAdapter.setResults(null);
        loadMoviesData();

        return super.onOptionsItemSelected(item);
    }

    private class AClass extends AsyncTask<URL,Void,Movie[]> {
        @Override
        protected Movie[] doInBackground(URL... params) {
            URL moviesUrl = params[0];
            String moviesResultString;

            try {
                moviesResultString = NetworkUtils.getResponseFromHttpUrl(moviesUrl);
                moviesResult = ObjectJsonUtils.setMovieResults(moviesResultString);

            }catch (JSONException| IOException e){
                e.printStackTrace();
            }
            return moviesResult;
        }

        @Override
        protected void onPostExecute(Movie[] s) {
            super.onPostExecute(s);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (s!=null && s.length!=0){
                mMoviesAdapter.setResults(s);
                showMoviesDataView();

            } else {
                showErrorMessage();
            }
        }
    }

    private Movie[] getFavoriteMovies(){
        Cursor cursor = mDb.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Movie[] movies= new Movie[cursor.getCount()];
        for (int i=0; i<movies.length; i++){
            cursor.moveToPosition(i);
            movies[i]= new Movie(cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)),
                    cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                    cursor.getFloat(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                    cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW)));
        }

        return movies;
    }


}
