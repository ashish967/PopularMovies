package udacity.popular_movies.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import udacity.popular_movies.BuildConfig;
import udacity.popular_movies.R;
import udacity.popular_movies.adapter.MoviesAdapter;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.datatypes.MoviesListResult;
import udacity.popular_movies.utils.AppUtils;


public class PopularMoviesFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String CURRENT_PAGE = "current_page";
    private static final String TAG = PopularMoviesFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    GridView mGridViewPopularMovies;
    ArrayList<Movie> mMoviesList= new ArrayList<>();
    MoviesAdapter mAdapter;
    int mPage;
    boolean mLoading;



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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view=inflater.inflate(R.layout.fragment_poplular_movies, container, false);

        initViews(view);

        setRetainInstance(true);
        if(savedInstanceState!=null){

            mPage= savedInstanceState.getInt(CURRENT_PAGE,1);
        }


        makeApiCall();

        return view;
    }

    private void initViews(View view) {


        mGridViewPopularMovies = (GridView) view.findViewById(R.id.gridview_popularmovies);

        mAdapter=new MoviesAdapter(getActivity(),mMoviesList);

        mGridViewPopularMovies.setAdapter(mAdapter);
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

                    Toast.makeText(getActivity(), "Loading More Movies", Toast.LENGTH_SHORT).show();
                    makeApiCall();

                } else if (mPage == 0) {
//                    Toast.makeText(getContext(),"All Page loaded",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        mPage=1;



    }

    private void makeApiCall() {


        mLoading=true;
        Call<MoviesListResult> call= PopularMovieApplication.mMoviesApiService.getMoviesList(BuildConfig.MOVIE_DB_API_KEY, mPage, AppUtils.getSortByOption());

        Log.d(TAG, "Sortby " + AppUtils.getSortByOption());
        call.enqueue(new Callback<MoviesListResult>() {
            @Override
            public void onResponse(Response<MoviesListResult> response, Retrofit retrofit) {
//                Log.d(TAG, response.body() + "");
                mLoading=false;

                MoviesListResult result= response.body();

                if(result==null){
                    return;
                }

                if(result.getResults()!=null) {
                    mMoviesList.addAll(result.getResults());
                    mPage++;
                    Log.d(TAG, "List size " + mMoviesList.size());
                    mAdapter.notifyDataSetChanged();

                }
                else{
                    mPage=0;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mLoading=false;
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

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

        mListener.onPosterClick(mMoviesList.get(position));
    }

    public void refreshData() {

        mMoviesList.clear();
        mPage=1;
        mAdapter.notifyDataSetChanged();

        makeApiCall();
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
        public void onPosterClick(Movie movie);
    }



}
