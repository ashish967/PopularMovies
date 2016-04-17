package udacity.popular_movies.application;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import udacity.popular_movies.services.ApiCalls;
import udacity.popular_movies.sync.SyncService;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public class PopularMovieApplication extends Application{


    public static Picasso mPicasso;
    public static Context mContext;


    public static String IMAGE_BASEURL="http://image.tmdb.org/t/p/";

    public static String BASE_URL="https://api.themoviedb.org/3/movie/";
    public static ApiCalls mMoviesApiService;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext= getApplicationContext();
        mPicasso= Picasso.with(getApplicationContext());

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        mMoviesApiService= retrofit.create(ApiCalls.class);

        SyncService.initialize();

    }
}
