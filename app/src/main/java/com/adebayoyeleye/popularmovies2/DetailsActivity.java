package com.adebayoyeleye.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adebayoyeleye.popularmovies2.data.MoviesContract;
import com.adebayoyeleye.popularmovies2.data.MoviesDbHelper;
import com.adebayoyeleye.popularmovies2.utilities.NetworkUtils;
import com.adebayoyeleye.popularmovies2.utilities.ObjectJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static android.support.v7.widget.RecyclerView.*;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>,TrailersAdapter.TrailersAdapterOnClickHandler, ReviewsAdapter.ReviewsAdapterOnClickHandler {

    public static final int OBJECT_LOADER = 22;
    public static final String BUNDLE_EXTRA = "path";

    private TextView mMovieTitleDisplay;
    private TextView mMovieReleaseDateDisplay;
    private TextView mMovieRatingDisplay;
    private TextView mMovieOverviewDisplay;
    private ImageView mPosterImageDisplay;
    private Button mFavouriteButton;

    private ProgressBar mLoadingIndicator;
    private Trailer[] trailers;
    private Review[] reviews;

    private RecyclerView mTrailerList;
    private TrailersAdapter mTrailersAdapter;
    private RecyclerView mReviewList;
    private ReviewsAdapter mReviewsAdapter;
    Movie movieClicked;
    private SQLiteDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mMovieTitleDisplay = (TextView) findViewById(R.id.tv_movie_title);
        mMovieReleaseDateDisplay = (TextView) findViewById(R.id.tv_movie_release_date);
        mMovieRatingDisplay = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieOverviewDisplay = (TextView) findViewById(R.id.tv_movie_overview);
        mPosterImageDisplay = (ImageView) findViewById(R.id.iv_movie_poster_details);
        mFavouriteButton = (Button) findViewById(R.id.btn_favourite);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mTrailerList = (RecyclerView) findViewById(R.id.rv_trailers);
        mReviewList = (RecyclerView) findViewById(R.id.rv_reviews);
        LayoutManager trailersLayoutManager= new LinearLayoutManager(this);
        LayoutManager reviewsLayoutManager= new LinearLayoutManager(this);
        mTrailersAdapter = new TrailersAdapter(this,this);
        mTrailerList.setLayoutManager(trailersLayoutManager);
        mTrailerList.setAdapter(mTrailersAdapter);
        mReviewsAdapter = new ReviewsAdapter(this,this);
        mReviewList.setLayoutManager(reviewsLayoutManager);
        mReviewList.setAdapter(mReviewsAdapter);

        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();


        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Movie.MOVIE_EXTRA)) {
                movieClicked = intentThatStartedThisActivity.getParcelableExtra(Movie.MOVIE_EXTRA);
                String rating = String.valueOf(movieClicked.getVoteAverage())+"/10";

                mMovieTitleDisplay.setText(movieClicked.getTitle());
                mMovieReleaseDateDisplay.setText(movieClicked.getReleaseDate());
                mMovieRatingDisplay.setText(rating);
                mMovieOverviewDisplay.setText(movieClicked.getOverview());

                Picasso.with(this)
                        .load("http://image.tmdb.org/t/p/w500/"+movieClicked.getPosterPath())
                        .into(mPosterImageDisplay);

//                        .placeholder(R.drawable.iv_loading)
//                        .error(R.drawable.ic_action_name)

                if (savedInstanceState != null) {
                    if (savedInstanceState.containsKey(Trailer.TRAILER_EXTRA)&& savedInstanceState.containsKey(Review.REVIEW_EXTRA)) {
                        Trailer[] savedTrailers = (Trailer[]) savedInstanceState
                                .getParcelableArray(Trailer.TRAILER_EXTRA);
                        Review[] savedReviews = (Review[]) savedInstanceState
                                .getParcelableArray(Review.REVIEW_EXTRA);
                        mTrailersAdapter.setResults(savedTrailers);
                        mReviewsAdapter.setResults(savedReviews);
                    }
                } else {
                    loadTrailers(movieClicked);
                }

                String favouriteStatus = checkFavouriteStatus() ? "Favourited" : "Add to Favourites";
                mFavouriteButton.setText(favouriteStatus);

            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(Trailer.TRAILER_EXTRA, trailers);
        outState.putParcelableArray(Review.REVIEW_EXTRA, reviews);
    }


    void loadTrailers(Movie movieClicked){
        Bundle loadBundle = new Bundle();
        loadBundle.putInt(BUNDLE_EXTRA, movieClicked.getId());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String[]> loader = loaderManager.getLoader(OBJECT_LOADER);
        if (loader==null){
            loaderManager.initLoader(OBJECT_LOADER,loadBundle,this);
        } else {
            loaderManager.restartLoader(OBJECT_LOADER,loadBundle,this);
        }

    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null){
                    return;
                }
                forceLoad();
//                mLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public String[] loadInBackground() {
                int movieId = args.getInt(BUNDLE_EXTRA);
                String videosPath = movieId+"/trailers";
                String reviewsPath = movieId+"/reviews";

                URL videosUrl = NetworkUtils.buildUrl(videosPath, DetailsActivity.this);
                URL reviewsUrl = NetworkUtils.buildUrl(reviewsPath, DetailsActivity.this);
                try {
                    String[] searchResults = {NetworkUtils.getResponseFromHttpUrl(videosUrl)
                            ,NetworkUtils.getResponseFromHttpUrl(reviewsUrl)};
                    return searchResults;
                }catch (IOException e){
                    e.printStackTrace();
                    return null;
                }

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        try {
            trailers = ObjectJsonUtils.setTrailerResults(data[0]);
            reviews = ObjectJsonUtils.setReviewResults(data[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTrailersAdapter.setResults(trailers);
        mReviewsAdapter.setResults(reviews);

    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }



    @Override
    public void onClick(Trailer trailerClicked) {
        String stringUrl = "https://www.youtube.com/watch?v=" + trailerClicked.getKey();
        Uri uri = Uri.parse(stringUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (intent.resolveActivity(getPackageManager()) !=null){
            startActivity(intent);
        }
    }

    @Override
    public void onClick(Review reviewClicked) {
        Uri uri = Uri.parse(reviewClicked.getUrl());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        if (intent.resolveActivity(getPackageManager()) !=null){
            startActivity(intent);
        }
    }

    void updateFavourite(View view) {
        if (mFavouriteButton.getText()=="Add to Favourites"){
            addToFavourites(movieClicked);
            mFavouriteButton.setText("Favourited");
        } else {
            if (removeFromFavourites(movieClicked.getId())){
                mFavouriteButton.setText("Add to Favourites");
            }
        }

    }

    private boolean removeFromFavourites(int movieID) {
        return mDb.delete(MoviesContract.MovieEntry.TABLE_NAME,
                MoviesContract.MovieEntry._ID + "=?",
                new String[] {String.valueOf(movieID)}) > 0;
    }

    private long addToFavourites (Movie movieClicked){
            ContentValues cv = new ContentValues();
            cv.put(MoviesContract.MovieEntry._ID,movieClicked.getId());
            cv.put(MoviesContract.MovieEntry.COLUMN_TITLE,movieClicked.getTitle());
            cv.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH,movieClicked.getPosterPath());
            cv.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,movieClicked.getReleaseDate());
            cv.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,movieClicked.getVoteAverage());
            cv.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,movieClicked.getOverview());

            return mDb.insert(MoviesContract.MovieEntry.TABLE_NAME,null,cv);
    }

    private boolean checkFavouriteStatus(){
        Cursor cursor = mDb.query(MoviesContract.MovieEntry.TABLE_NAME,
                new String[] {MoviesContract.MovieEntry._ID},
                MoviesContract.MovieEntry._ID + "=?",
                new String[] {String.valueOf(movieClicked.getId())},
                null,
                null,
                null);
        return cursor.getCount()>0;
    }


}
