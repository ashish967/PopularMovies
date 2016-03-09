package udacity.popular_movies.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import udacity.popular_movies.R;
import udacity.popular_movies.activity.MovieDetailActivity;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.utils.AppUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {


    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private Movie mMovie;

    public static MovieDetailFragment getNewInstance(Movie movie){


        MovieDetailFragment fragment= new MovieDetailFragment();

        Bundle bundle=new Bundle();
        bundle.putSerializable(MovieDetailActivity.MOVIE,movie);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view=inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mMovie= (Movie) getArguments().getSerializable(MovieDetailActivity.MOVIE);


        initViews(view);
        return view;
    }

    private void initViews(View view) {


        ImageView poster= (ImageView) view.findViewById(R.id.iv_posterview);
        ImageView coverimage= (ImageView) view.findViewById(R.id.iv_coverimage);
        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getPoster_path()))
                .into(poster);

        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(mMovie.getBackdrop_path()))
                .into(coverimage);


        TextView title= (TextView) view.findViewById(R.id.tv_movie_title);
        TextView release= (TextView) view.findViewById(R.id.tv_release_year);
        TextView users_rating= (TextView) view.findViewById(R.id.tv_duration);
        TextView votes= (TextView) view.findViewById(R.id.tv_rating);
        TextView overview= (TextView) view.findViewById(R.id.tv_overview);

        title.setText(mMovie.getTitle());

        SimpleDateFormat format= new SimpleDateFormat("yyyy");
        release.setText(format.format(mMovie.getRelease_date()));

        DecimalFormat rating_format= new DecimalFormat("##.00");
        users_rating.setText(rating_format.format(mMovie.getVote_average()) + " /" + 10);


        votes.setText(mMovie.getVote_count() + " Votes");
        overview.setText(mMovie.getOverview());

    }
}
