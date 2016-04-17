package udacity.popular_movies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import udacity.popular_movies.utils.Logger;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class MoviesProvider extends ContentProvider {

    private static final String TAG = MoviesProvider.class.getSimpleName();
    private MoviesDbHelper mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID=101;

    private static final SQLiteQueryBuilder sMovieIdSelection;

    static{
        sMovieIdSelection = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieIdSelection.setTables(
                MoviesContract.MovieEntry.TABLE_NAME );
    }


    private static final String sMovieSelection=

            MoviesContract.MovieEntry.TABLE_NAME+"."+MoviesContract.MovieEntry._ID+"=?";


    private Cursor getMovieById(Uri uri,String [] projection,String sortby){


        String movieId= MoviesContract.MovieEntry.getMovieIdFromUri(uri);
        Logger.log(TAG,"Movie id"+movieId);
        return sMovieIdSelection.query(mOpenHelper.getReadableDatabase(),
                projection,sMovieSelection,new String[]{movieId},null,null,sortby);


    }

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;



            }
            case MOVIE_WITH_ID:{

                retCursor = getMovieById(uri,projection,sortOrder);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_TYPE;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MoviesContract.MovieEntry.buidMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                rowsDeleted+= db.delete(MoviesContract.FavoriteMovies.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                rowsUpdated+= db.update(MoviesContract.FavoriteMovies.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        udacity.popular_movies.utils.Logger.log(TAG," MATCH "+match);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Cursor cursor = db.rawQuery("INSERT OR REPLACE INTO "+
                                MoviesContract.MovieEntry.TABLE_NAME+" ( "+getColumnString()
                                +")"+
                                "VALUES ( "+getValuesString(value)+");",null);

                        ;
                        if ( cursor.moveToFirst()) {
                            returnCount++;
                        }
                    }
                    udacity.popular_movies.utils.Logger.log(TAG," insterted  "+values.length);

                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;



            default:
                return super.bulkInsert(uri, values);
        }
    }

    private String getColumnString() {

        return MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH+","
                +MoviesContract.MovieEntry.COLUMN_MOVIE_ID+","+
                MoviesContract.MovieEntry.COLUMN_ORGINAL_LANGUAGE+","+
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE+","+
                MoviesContract.MovieEntry.COLUMN_OVERVIEW+","+
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH+","+
                MoviesContract.MovieEntry.COLUMN_TITLE+","+
                MoviesContract.MovieEntry.COLUMN_POPULARITY+","+
                MoviesContract.MovieEntry.COLUMN_ADULT+","+
                MoviesContract.MovieEntry.COLUMN_VIDEO+","+
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE+","+
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT+","+
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE+","+
                MoviesContract.MovieEntry.COLUMN_FAVOURITE;

    }

    private String getValuesString(ContentValues value) {


        String original_title=value.getAsString(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        String title= value.getAsString(MoviesContract.MovieEntry.COLUMN_TITLE);
        String overview= value.getAsString(MoviesContract.MovieEntry.COLUMN_OVERVIEW);

        return "'"+ value.getAsString(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH)+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_MOVIE_ID)+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_ORGINAL_LANGUAGE)+"','"+
                ((original_title!=null)?original_title.replace("'","''"):"")+"','"+
                ((overview!=null)?overview.replace("'", "''"):"")+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)+"','"+
                ((title!=null)?title.replace("'","''"):"")+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_POPULARITY)+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_ADULT)+"','"+
                value.getAsString(MoviesContract.MovieEntry.COLUMN_VIDEO)+"','"+
                value.getAsInteger(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE) + "','" +
                value.getAsInteger(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT )+ "','" +
                value.getAsInteger(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE) + "',"+
                "COALESCE ( (SELECT " + MoviesContract.MovieEntry.COLUMN_FAVOURITE + " FROM "
                + MoviesContract.MovieEntry.TABLE_NAME + " WHERE "
                + MoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = "
                + value.getAsString(MoviesContract.MovieEntry.COLUMN_MOVIE_ID) + "), 'false')"
                ;
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
