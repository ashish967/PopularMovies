package udacity.popular_movies.fragment;

import io.realm.Realm;
import udacity.popular_movies.adapter.MoviesAdapter;
import udacity.popular_movies.datatypes.RealmMovie;

/**
 * Created by ashish-novelroots on 22/4/16.
 */
public class FavoriteMoviesFragment extends PopularMoviesFragment {


    @Override
    protected void setAdapter() {

        Realm realm = Realm.getInstance(getActivity());
        mResults = realm.where(RealmMovie.class)
                .equalTo("favourite",true)
                .findAll();

        mAdapter= new MoviesAdapter(getActivity(),mResults,true,mTwoPan);

        if(mTwoPan){

            mGridViewPopularMovies.setNumColumns(1);
        }
        else{
            mGridViewPopularMovies.setNumColumns(2);
        }

        mGridViewPopularMovies.setAdapter(mAdapter);
    }

    @Override
    protected void loadMoreMovies() {

    }
}
