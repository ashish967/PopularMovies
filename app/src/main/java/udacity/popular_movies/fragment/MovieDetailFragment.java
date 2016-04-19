package udacity.popular_movies.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
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
import udacity.popular_movies.datatypes.RealmMovie;
import udacity.popular_movies.datatypes.ReviewType;
import udacity.popular_movies.datatypes.ReviewsListResult;
import udacity.popular_movies.datatypes.VideoType;
import udacity.popular_movies.datatypes.VideosResult;
import udacity.popular_movies.utils.AppUtils;
import udacity.popular_movies.utils.Logger;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {


    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final int LOAD_MOVIE = 1;
    private RealmMovie mMovie;
    View mView;


    String mUri;
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
    private Menu mMenu;

    ImageView mIvFavIcon;

    Realm mRealm;
    private View mHeaderView;
    private View mToolBar;
    private boolean mTwospan;

    public static MovieDetailFragment getNewInstance(String movie){


        MovieDetailFragment fragment= new MovieDetailFragment();

        Bundle bundle=new Bundle();
        bundle.putString(MovieDetailActivity.MOVIE, movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){

            mUri= getArguments().getString(MovieDetailActivity.MOVIE);

        }
    }

    public MovieDetailFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mRealm= Realm.getInstance(getContext());
        mView=inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mView.setVisibility(View.GONE);


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

        updateDb();
        updateIcon();

    }

    private void updateDb() {

        mRealm.beginTransaction();
        mMovie.setFavourite(!mMovie.isFavourite());
        mRealm.commitTransaction();

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

        RealmResults<RealmMovie> results= mRealm.where(RealmMovie.class)
                                            .equalTo("id",mUri)
                                            .findAll();

        mMovie= results.get(0);
        initViews(mView);

        super.onActivityCreated(savedInstanceState);
    }

    private void initViews(View view) {


        mView.setVisibility(View.VISIBLE);
        mActivity = (AppCompatActivity) getActivity();


        mAdapter = new ReviewsAdapter(mActivity, mReviewList);
        mToolBar = view.findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mIvFavIcon  = (ImageView) view.findViewById(R.id.iv_fav_icon);
        ImageView backIcon= (ImageView) view.findViewById(R.id.iv_back_icon);

        if(mTwospan){
            backIcon.setVisibility(View.GONE);
        }
        mIvFavIcon.setOnClickListener(this);
        backIcon.setOnClickListener(this);
        mLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFooter = mActivity.getLayoutInflater().inflate(R.layout.list_footer, null);
        mFooter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mHeaderView = mActivity.getLayoutInflater().inflate(R.layout.movie_detail_header, null);
        mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ImageView poster = (ImageView) mHeaderView.findViewById(R.id.iv_posterview);
        ImageView coverimage = (ImageView) mHeaderView.findViewById(R.id.iv_coverimage);
        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getPoster_path()))
                .into(poster);

        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getBackdrop_path()))
                .into(coverimage);

        TextView action_bar_title= (TextView) mView.findViewById(R.id.tv_movie_title);
        TextView title = (TextView) mHeaderView.findViewById(R.id.tv_movie_title);
        TextView release = (TextView) mHeaderView.findViewById(R.id.tv_release_year);
        TextView users_rating = (TextView) mHeaderView.findViewById(R.id.tv_duration);
        TextView votes = (TextView) mHeaderView.findViewById(R.id.tv_rating);
        TextView overview = (TextView) mHeaderView.findViewById(R.id.tv_overview);

        title.setText(mMovie.getTitle());
        action_bar_title.setText(mMovie.getTitle());
        release.setText(mMovie.getRelease_date());

        DecimalFormat rating_format = new DecimalFormat("##.00");
        users_rating.setText(rating_format.format(mMovie.getVote_average()) + " /" + 10);


        votes.setText(mMovie.getVote_count() + " Votes");
        overview.setText(mMovie.getOverview());



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

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                updateActionBar(dy);
            }
        });


        makeVideoRequest();


        updateIcon();
        updateActionBar(0);
    }

    private void updateActionBar(int dy) {


        Logger.log(TAG, "header top " + mHeaderView.getTop());
        dy=-mHeaderView.getTop();

        if(dy<0){

            dy=0;
        }
        else if( dy > getResources().getDisplayMetrics().density*300){

            dy= (int) (getResources().getDisplayMetrics().density*300);
        }

        float ratio = (dy*1.0f) / (getResources().getDisplayMetrics().density*300);
        Logger.log(TAG, "Dy " + dy + " ratio " + ratio);

        mToolBar.setBackgroundColor(getFilteredColor(0xFFF5f5f5, ratio));

    }

    public static int getFilteredColor(int mEventColor, float ratio) {

        return  Color.argb((int) (255 * ratio), Color.red(mEventColor), Color.green(mEventColor), Color.blue(mEventColor));

    }

    private void updateIcon() {
        mIvFavIcon.setImageResource(mMovie.isFavourite()?R.drawable.eventdetails_love_pressed:R.drawable.eventdetails_love);
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

                mTrailerResult = result.getResults();

                LinearLayout trailerContainer = (LinearLayout) mView.findViewById(R.id.trailer_containers);

                for (int i = 0; i < mTrailerResult.size(); i++) {

                    View view = LayoutInflater.from(PopularMovieApplication.mContext).inflate(R.layout.trailer_item, trailerContainer, false);
                    TextView trailer = (TextView) view.findViewById(R.id.tv_trailer);
                    trailer.setText("Trailer " + (i + 1));
                    view.setOnClickListener(MovieDetailFragment.this);
                    view.setTag(i);
                    trailerContainer.addView(view);
                    Logger.log(TAG,"Added trailer "+i);
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



    private void makeApiCall(final int page) {



        if(mLoading){
            Logger.log(TAG, "Already  loading...");
            return;
        }
        Call<ReviewsListResult> call= PopularMovieApplication.mMoviesApiService.getReviewsList(mMovie.getId(), BuildConfig.MOVIE_DB_API_KEY, page);

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


    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.iv_back_icon:
                 getActivity().onBackPressed();
                 break;
            case R.id.iv_fav_icon:
                 toggleFavorate();
                break;

            case R.id.trailer_item:
                handleTrailerAction(v);
        }
    }

    private void handleTrailerAction(View v) {


        int pos= (int) v.getTag();

        String id=mTrailerResult.get(pos).getKey();

        String url = "http://www.youtube.com/watch?v=" +id;
        Logger.log(TAG, url);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public void setTwoSpan(boolean b) {

        mTwospan= b;
    }
}
