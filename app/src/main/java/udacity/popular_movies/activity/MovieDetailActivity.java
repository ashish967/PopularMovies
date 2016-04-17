package udacity.popular_movies.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import udacity.popular_movies.R;
import udacity.popular_movies.fragment.MovieDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE = "MOVIE";

    private Uri mUri;


    public static Intent createIntent(Context context,Uri movie){

        Intent intent= new Intent(context,MovieDetailActivity.class);
        intent.putExtra(MOVIE,movie);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getExtras();

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().
                    add(R.id.content, MovieDetailFragment.getNewInstance(mUri)).commit();
        }



    }

    private void getExtras() {

        mUri= getIntent().getParcelableExtra(MOVIE);

    }


}
