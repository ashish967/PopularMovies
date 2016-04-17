package udacity.popular_movies.sync;

import android.util.Log;

import com.squareup.okhttp.ResponseBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import udacity.popular_movies.BuildConfig;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.datatypes.RealmMovie;
import udacity.popular_movies.event.LoadDataEvent;
import udacity.popular_movies.event.MessageEvent;
import udacity.popular_movies.utils.AppUtils;
import udacity.popular_movies.utils.Logger;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class SyncService {


    private static final String TAG = SyncService.class.getSimpleName();

    boolean mLoading;
    private static  Realm realm;

    private static SyncService mService;

    public static void initialize(){

        if(mService!=null){
            return;
        }

        realm = Realm.getInstance(PopularMovieApplication.mContext);

        mService= new SyncService();
        EventBus.getDefault().register(mService);
        Logger.log(TAG, "Initialize");
    }

    @Subscribe
    public void onEvent(LoadDataEvent event){

        Logger.log(TAG, "load page with page number " + event.mPage);
        makeApiCall(event.mPage);
    }

    private void makeApiCall(final int page) {



        if(mLoading){
            Logger.log(TAG,"Already  loading...");
            return;
        }
        Call<ResponseBody> call= PopularMovieApplication.mMoviesApiService.getMoviesList(BuildConfig.MOVIE_DB_API_KEY, page, AppUtils.getSortByOption());

        Log.d(TAG, "Sortby " + AppUtils.getSortByOption());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {


                String json= null;
                try {
                    json = response.body().string();
                    Log.d(TAG, json+ "");

                    insertMoviesToDb(json);
                    EventBus.getDefault().post(new MessageEvent(true, "Loaded"));

                    mLoading = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Throwable t) {
                EventBus.getDefault().post(new MessageEvent(false, "No internet connection"));
                t.printStackTrace();

                mLoading = false;
            }
        });

        mLoading=true;
    }

    private static void insertMoviesToDb(String movies) {


        realm.beginTransaction();;

        RealmQuery<RealmMovie> query = realm.where(RealmMovie.class);
        query.equalTo("favourite",true);
        RealmResults<RealmMovie> result1 = query.findAll();

        HashMap<String,Boolean> map= new HashMap<>();

        for(int i=0;i<result1.size();i++){

            Logger.log(TAG,"Fav movie "+result1.get(i).getTitle());
            map.put(result1.get(i).getId(),true);
        }

        try {
            JSONObject jsonObject= new JSONObject(movies);

            JSONArray array= jsonObject.getJSONArray("results");

            Logger.log(TAG,"inserting into Db");

            if(array!=null){

                for(int i=0;i<array.length();i++){

                    JSONObject object= array.getJSONObject(i);

                    if(map.get(object.getString("id"))!=null){

                        object.put("favourite",true);
                    }

                    realm.createOrUpdateObjectFromJson(RealmMovie.class, object.toString(4));

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        realm.commitTransaction();



    }


}
