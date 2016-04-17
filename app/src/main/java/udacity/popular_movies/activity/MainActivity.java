package udacity.popular_movies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import udacity.popular_movies.R;
import udacity.popular_movies.fragment.PopularMoviesFragment;
import udacity.popular_movies.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.OnFragmentInteractionListener{



    public static String TAG= MainActivity.class.getSimpleName();

    String mSortBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPosterClick(Uri movie) {

//        Toast.makeText(this,"Movie: "+movie.getTitle(),Toast.LENGTH_SHORT).show();

        Intent intent= MovieDetailActivity.createIntent(this,movie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void handleSettingsAction() {


        Intent intent= new Intent(this,SettingsActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();


        if(mSortBy==null){

            mSortBy= AppUtils.getSortByOption();
        }
        else if(!mSortBy.equals(AppUtils.getSortByOption())){

            mSortBy=AppUtils.getSortByOption();
            PopularMoviesFragment fragment= (PopularMoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_popularmovies);
            fragment.refreshData();


        }
    }
}
