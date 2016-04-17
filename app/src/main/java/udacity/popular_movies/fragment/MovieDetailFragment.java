package udacity.popular_movies.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import udacity.popular_movies.BuildConfig;
import udacity.popular_movies.R;
import udacity.popular_movies.activity.MovieDetailActivity;
import udacity.popular_movies.activity.SettingsActivity;
import udacity.popular_movies.adapter.ReviewsAdapter;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.data.MoviesContract;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.datatypes.ReviewType;
import udacity.popular_movies.datatypes.ReviewsListResult;
import udacity.popular_movies.datatypes.VideoType;
import udacity.popular_movies.datatypes.VideosResult;
import udacity.popular_movies.utils.AppUtils;
import udacity.popular_movies.utils.Logger;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final int LOAD_MOVIE = 1;
    private Movie mMovie;
    View mView;


    Uri mUri;
    private boolean mLoading;

    ArrayList<ReviewType> mReviewList= new ArrayList<>();

    ReviewsAdapter mAdapter;

    AppCompatActivity mActivity;

    protected LinearLayoutManager mLayoutManager;

    View  mFooter;

    private RecyclerView mRecyclerView;

    private int mNext;
    private ArrayList<VideoType> mTrailerResult
            ;
    private Button mBtnTrailer;
    private Menu mMenu;

    public static MovieDetailFragment getNewInstance(Uri movie){


        MovieDetailFragment fragment= new MovieDetailFragment();

        Bundle bundle=new Bundle();
        bundle.putParcelable(MovieDetailActivity.MOVIE, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){

            mUri= getArguments().getParcelable(MovieDetailActivity.MOVIE);

        }
    }

    public MovieDetailFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mView=inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mView.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) mView.findViewById(R.id.toolbar);

        //set toolbar appearance

        //for crate home button
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        return mView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                break;

            case  R.id.action_settings:
                    handleSettingsAction();
                break;

            case  R.id.action_fav:
                toggleFavorate();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorate() {

        mMovie.setFavourite(!mMovie.isFavourite());
        updateIcon();
        updateDb();
    }

    private void updateDb() {



    }

    private void handleSettingsAction() {


        Intent intent= new Intent(mActivity,SettingsActivity.class);
        startActivity(intent);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);
        mMenu= menu;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {


        getLoaderManager().initLoader(LOAD_MOVIE, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(View view) {


        mView.setVisibility(View.VISIBLE);
        mActivity = (AppCompatActivity) getActivity();


        mAdapter = new ReviewsAdapter(mActivity, mReviewList);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFooter = mActivity.getLayoutInflater().inflate(R.layout.list_footer, null);
        mFooter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View mHeaderView = mActivity.getLayoutInflater().inflate(R.layout.movie_detail_header, null);
        mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView poster = (ImageView) mHeaderView.findViewById(R.id.iv_posterview);
        ImageView coverimage = (ImageView) mHeaderView.findViewById(R.id.iv_coverimage);
        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getPoster_path()))
                .into(poster);

        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getBackdrop_path()))
                .into(coverimage);


        TextView title = (TextView) mHeaderView.findViewById(R.id.tv_movie_title);
        TextView release = (TextView) mHeaderView.findViewById(R.id.tv_release_year);
        TextView users_rating = (TextView) mHeaderView.findViewById(R.id.tv_duration);
        TextView votes = (TextView) mHeaderView.findViewById(R.id.tv_rating);
        TextView overview = (TextView) mHeaderView.findViewById(R.id.tv_overview);

        title.setText(mMovie.getTitle());

        release.setText(mMovie.getRelease_date());

        DecimalFormat rating_format = new DecimalFormat("##.00");
        users_rating.setText(rating_format.format(mMovie.getVote_average()) + " /" + 10);


        votes.setText(mMovie.getVote_count() + " Votes");
        overview.setText(mMovie.getOverview());


        mBtnTrailer= (Button) mHeaderView.findViewById(R.id.btn_trailer);
        mBtnTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://www.youtube.com/watch?v=" + mTrailerResult.get(0).getId();
                Logger.log(TAG, url);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        mBtnTrailer.setVisibility(View.VISIBLE);
        mAdapter.setHeader(mHeaderView);
        mAdapter.setFooter(mFooter);
        mNext = 1;

        makeApiCall(mNext);


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                Logger.log(TAG, "onScrollStateChanged");

                if (shouldLoadMore()) {
                    showLoadMoreView();
                    makeApiCall(mNext);

                } else if (!mLoading && mNext == 0 && mAdapter != null) {
                    hideFooterView();
                    mAdapter.notifyDataSetChanged();
                }
            }

        });


        makeVideoRequest();


        updateIcon();
    }

    private void updateIcon() {


        mMenu.findItem(R.id.action_fav).setIcon(mMovie.isFavourite()?R.drawable.star_fill:R.drawable.star_empty);

    }



    private void makeVideoRequest() {


        Call<VideosResult> call= PopularMovieApplication.mMoviesApiService.getVideosList(mMovie.getId(), BuildConfig.MOVIE_DB_API_KEY);

        Log.d(TAG, "Sortby " + AppUtils.getSortByOption());
        call.enqueue(new Callback<VideosResult>() {
            @Override
            public void onResponse(Response<VideosResult> response, Retrofit retrofit) {
//                Log.d(TAG, response.body() + "");

                mLoading = false;

                VideosResult result = response.body();

                 mTrailerResult= result.getResults();

                if(mTrailerResult!=null||!mTrailerResult.isEmpty()){

                    mBtnTrailer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                hideFooterView();
                mLoading = false;
            }
        });

    }

    private void showLoadMoreView() {
        mFooter.setVisibility(View.VISIBLE);
    }

    protected void hideFooterView() {
        mFooter.setVisibility(View.GONE);

    }

    private boolean shouldLoadMore() {


        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
        Logger.log(TAG, "should load more " + visibleItemCount + " total " + totalItemCount + "  firstvisibleitem " + firstVisibleItemPosition + " next " + mNext + " mloading " + mLoading);
        if(!mLoading && mNext != 0&&((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 5)){

            return true;
        }

        return false;
    }

    public static final int COL_ROW_ID=0;
    public static final int COL_POSTER_PAHT=1;
    public static final int COL_RLEASE_DATE=2;
    public static final int COL_TITLE=3;
    public static final int COL_VOTE_AVERAGE=4;
    public static final int COL_VOTE_COUNT=5;
    public static final int COL_OVERVIEW=6;
    public static final int COL_BACKDROP_PATH=7;
    public static final int COL_MOVIE_ID=8;
    public static final int COL_IS_FAVORITE=9;

    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_VOTE_COUNT,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_FAVOURITE

    };
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            Logger.log(TAG," id "+MoviesContract.MovieEntry.getMovieIdFromUri(mUri));
            return   new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    MoviesContract.MovieEntry._ID+"=?",
                    null,
                    null
            );
        }
        return null;


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            int movieId = data.getInt(COL_MOVIE_ID);

            mMovie= new Movie();
            mMovie.setTitle(data.getString(COL_TITLE));
            mMovie.setOverview(data.getString(COL_OVERVIEW));
            mMovie.setBackdrop_path(data.getString(COL_BACKDROP_PATH));
            mMovie.setPoster_path(data.getString(COL_POSTER_PAHT));
            mMovie.setVote_count(data.getInt(COL_VOTE_COUNT));
            mMovie.setVote_average(data.getFloat(COL_VOTE_AVERAGE));
            mMovie.setId(data.getString(COL_MOVIE_ID));
            mMovie.setFavourite(Boolean.valueOf(data.getString(COL_IS_FAVORITE)));
            long date= data.getLong(COL_RLEASE_DATE);
            SimpleDateFormat format=new SimpleDateFormat("yyyy");
            String release_date=format.format(new Date(date));
            mMovie.setRelease_date(release_date);

            initViews(mView);
        }
        }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void makeApiCall(final int page) {



        if(mLoading){
            Logger.log(TAG, "Already  loading...");
            return;
        }
        Call<ReviewsListResult> call= PopularMovieApplication.mMoviesApiService.getReviewsList(mMovie.getId(),BuildConfig.MOVIE_DB_API_KEY, page );

        Log.d(TAG, "Sortby " + AppUtils.getSortByOption());
        call.enqueue(new Callback<ReviewsListResult>() {
            @Override
            public void onResponse(Response<ReviewsListResult> response, Retrofit retrofit) {
//                Log.d(TAG, response.body() + "");

                mLoading = false;

                ReviewsListResult result = response.body();

                if (result == null) {
                    return;
                }

                if (result.getResults() != null && result.getResults().size() > 0) {

                    if (page == 1) {
                        mReviewList.clear();
                    }

                    mReviewList.addAll(result.getResults());
                    mNext++;
                } else {
                    mNext = 0;
                }

                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                hideFooterView();
                mLoading = false;
            }
        });

        mLoading=true;
    }


}
