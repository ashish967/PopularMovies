package udacity.popular_movies.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import udacity.popular_movies.R;
import udacity.popular_movies.fragment.FavoriteMoviesFragment;

public class FavoriteMoviesActivity extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    protected void init() {

        setContentView(R.layout.activity_favorite_movies);


        findViewById(R.id.iv_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        FavoriteMoviesFragment fragment= (FavoriteMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_popularmovies);

        if(findViewById(R.id.movie_detail_container)!=null){
            fragment.setTwoPane(true);
            mTwoPane=true;
        }
        else{
            fragment.setTwoPane(false);
            mTwoPane=false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
