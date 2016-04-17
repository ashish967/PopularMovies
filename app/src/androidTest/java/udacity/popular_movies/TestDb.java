package udacity.popular_movies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

import udacity.popular_movies.data.MoviesContract;
import udacity.popular_movies.data.MoviesDbHelper;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(MoviesContract.MovieEntry._ID);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_FAVOURITE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_MOVIE_ID);;
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_ORGINAL_LANGUAGE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_TITLE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POPULARITY);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_ADULT);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VIDEO);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
        locationColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testMovieTable(){

        insertMovie();
    }

    private long insertMovie() {

        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createMovieValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long movieRowId;
        movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        
        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from location query",
                cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return movieRowId;
    }


}
