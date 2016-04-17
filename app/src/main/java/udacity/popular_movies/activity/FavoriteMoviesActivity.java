package udacity.popular_movies.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import io.realm.Realm;
import io.realm.RealmResults;
import udacity.popular_movies.R;
import udacity.popular_movies.adapter.MoviesAdapter;
import udacity.popular_movies.datatypes.RealmMovie;

public class FavoriteMoviesActivity extends AppCompatActivity {



    GridView mGridViewPopularMovies;

    MoviesAdapter mAdapter;
    private RealmResults<RealmMovie> mResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movies);


        findViewById(R.id.iv_back_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        mGridViewPopularMovies = (GridView) findViewById(R.id.gridview_popularmovies);
        mGridViewPopularMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                Intent intent= MovieDetailActivity.createIntent(FavoriteMoviesActivity.this,mResults.get(position).getId());
                startActivity(intent);
            }
        });

        Realm realm = Realm.getInstance(this);
        mResults = realm.where(RealmMovie.class)
                    .equalTo("favourite",true)
                    .findAll();

        mAdapter= new MoviesAdapter(this,mResults,true);
        mGridViewPopularMovies.setAdapter(mAdapter);


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
