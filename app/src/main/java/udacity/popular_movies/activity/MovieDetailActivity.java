package udacity.popular_movies.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import udacity.popular_movies.R;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.fragment.MovieDetailFragment;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String MOVIE = "MOVIE";

    Movie mMovie;


    public static Intent createIntent(Context context,Movie movie){

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
                    add(R.id.content, MovieDetailFragment.getNewInstance(mMovie)).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getExtras() {

        mMovie= (Movie) getIntent().getSerializableExtra(MOVIE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
            handleSettingsAction();
        }
        else if(id== android.R.id.home){

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleSettingsAction() {


        Intent intent= new Intent(this,SettingsActivity.class);
        startActivity(intent);

    }
}
