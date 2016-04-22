package udacity.popular_movies.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import udacity.popular_movies.R;
import udacity.popular_movies.adapter.MoviesAdapter;
import udacity.popular_movies.data.MoviesContract;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.datatypes.RealmMovie;
import udacity.popular_movies.event.LoadDataEvent;
import udacity.popular_movies.event.MessageEvent;
import udacity.popular_movies.utils.AppUtils;
import udacity.popular_movies.utils.Logger;


public class PopularMoviesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String CURRENT_PAGE = "current_page";
    private static final String TAG = PopularMoviesFragment.class.getSimpleName();
    private static final int MOVIES_LOADER = 1;
    private OnFragmentInteractionListener mListener;

    GridView mGridViewPopularMovies;
    ArrayList<Movie> mMoviesList= new ArrayList<>();
    MoviesAdapter mAdapter;
    int mPage;
    boolean mLoading;

    View mFooter;
    private static final String[] MOVIES_COLUMNS={

        MoviesContract.MovieEntry.TABLE_NAME+"."+ MoviesContract.MovieEntry._ID
        ,MoviesContract.MovieEntry.COLUMN_POSTER_PATH

    };

    public static final int MOVIE_ROW_ID=0;
    public static final int POSTER_PATH=1;
    protected RealmResults<RealmMovie> mResults;


    PopupMenu mMenu;
    protected boolean mTwoPan;


    public static PopularMoviesFragment newInstance() {
        PopularMoviesFragment fragment = new PopularMoviesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PopularMoviesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        setRetainInstance(true);
        View view=inflater.inflate(R.layout.fragment_poplular_movies, container, false);

        initViews(view);

        setRetainInstance(true);
        if(savedInstanceState!=null){

            mPage= savedInstanceState.getInt(CURRENT_PAGE,1);
        }





        return view;
    }

    private void initViews(View view) {


        mGridViewPopularMovies = (GridView) view.findViewById(R.id.gridview_popularmovies);


        mFooter=  view.findViewById(R.id.footer_loader);
        mGridViewPopularMovies.setOnItemClickListener(this);

        mGridViewPopularMovies.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


                if (scrollState != SCROLL_STATE_IDLE) {

                    return;

                }

                int pastVisiblesItems = view.getFirstVisiblePosition();
                int visibleItemCount = view.getChildCount();
                int totalItemCount = view.getCount();


                // we have reached at the end
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 5 && !mLoading && mPage != 0) {

                    loadMoreMovies();

                } else if (mPage == 0) {
//                    Toast.makeText(getContext(),"All Page loaded",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mPage=1;
        EventBus.getDefault().post(new LoadDataEvent(mPage));

        setAdapter();


    }

    protected void loadMoreMovies() {

        Toast.makeText(getActivity(), "Loading More Movies", Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new LoadDataEvent(mPage));
    }

    protected void setAdapter() {

        Realm realm = Realm.getInstance(getActivity());
        mResults = realm.allObjects(RealmMovie.class);

        mAdapter= new MoviesAdapter(getActivity(),mResults,true,mTwoPan);

        if(mTwoPan){

            mGridViewPopularMovies.setNumColumns(1);
        }
        else{
            mGridViewPopularMovies.setNumColumns(2);
        }

        mGridViewPopularMovies.setAdapter(mAdapter);

    }


    private void showFooter() {

        mFooter.setVisibility(View.VISIBLE);
    }

    private void hideFooter(){

        mFooter.setVisibility(View.GONE);
    }
    @Subscribe
    public void onMessageEvent(MessageEvent event){

        if(event.isSuccess()){

            mPage++;
            Logger.log(TAG,"Success event loadedd "+event.getMessage());
        }
        else{
            Toast.makeText(getActivity(),event.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE, mPage);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        mListener.onPosterClick(mResults.get(position).getId());
    }

    public void refreshData() {


        if(AppUtils.getSortByOption().equals(getResources().getString(R.string.pref_sortby_hightestrated))) {
            mResults.sort("vote_average", Sort.DESCENDING);
            Logger.log(TAG,"sort by vote avg");
        }
        else{
            mResults.sort("popularity", Sort.DESCENDING);
            Logger.log(TAG, "sort by popuplarity");

        }

        mPage=1;
        mAdapter.notifyDataSetChanged();

    }

    public void setTwoPane(boolean b) {

        mTwoPan=b;

        mAdapter= new MoviesAdapter(getActivity(),mResults,true,mTwoPan);

        if(mTwoPan){

            mGridViewPopularMovies.setNumColumns(1);
        }
        else{
            mGridViewPopularMovies.setNumColumns(2);
        }

        mGridViewPopularMovies.setAdapter(mAdapter);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void onPosterClick(String id);
    }



}
