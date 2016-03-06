package udacity.popular_movies.utils;

import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import udacity.popular_movies.R;
import udacity.popular_movies.application.PopularMovieApplication;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public class AppUtils {



    public static int convertDpToPixel(Context context, float f) {
        // TODO Auto-generated method stub
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) f * density);
    }
    public static double convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        double dp = px / (metrics.density );
        return Math.ceil(dp);
    }

    public static String createPosterUrl(String poster_path){

        return PopularMovieApplication.IMAGE_BASEURL+"/w185"+poster_path;

    }

    public static String getSortByOption(){

        return  PreferenceManager
                .getDefaultSharedPreferences(PopularMovieApplication.mContext)
                .getString(PopularMovieApplication.mContext.getString(R.string.pref_sortby_key),
                        PopularMovieApplication.mContext.getString(R.string.pref_sortby_label_popularity));
    }
}
