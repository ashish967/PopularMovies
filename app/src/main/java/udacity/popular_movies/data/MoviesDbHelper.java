package udacity.popular_movies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ashish-novelroots on 2/4/16.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {



    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT , " +
                MoviesContract.MovieEntry.COLUMN_FAVOURITE + " BOOLEAN NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_ORGINAL_LANGUAGE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " FLOAT , " +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " FLOAT , " +
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER , " +
                MoviesContract.MovieEntry.COLUMN_ADULT + " BOOLEAN , " +
                MoviesContract.MovieEntry.COLUMN_VIDEO + " BOOLEAN , " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " INTEGER " +
                " );";



        final String SQL_CREATE_FAV_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.FavoriteMovies.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.FavoriteMovies.COLUMN_MOVIE_ID + " TEXT , " +
                MoviesContract.FavoriteMovies.COLUMN_IS_FAVORITE + " BOOLEAN NOT NULL ";


                db.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.FavoriteMovies.TABLE_NAME);
        onCreate(db);
    }
}
