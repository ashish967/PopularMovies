package udacity.popular_movies.utils;

import android.util.Log;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class Logger {

    public static void log(String TAG,String msg){

        Log.d(TAG,msg);
    }

    public static void log(Exception e){

       e.printStackTrace();
    }
}
