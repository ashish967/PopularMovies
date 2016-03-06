package udacity.popular_movies.services;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import udacity.popular_movies.datatypes.MoviesListResult;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public interface ApiCalls {

    @GET("popular")
    Call<MoviesListResult> getMoviesList(@Query("api_key") String apikey,@Query("page") int page
    ,@Query("sortby") String sortby);

}
