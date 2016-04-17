package udacity.popular_movies;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import udacity.popular_movies.data.MoviesContract;

/**
 * Created by ashish-novelroots on 3/4/16.
 */
public class TestUtilities extends AndroidTestCase {

    static final long TEST_DATE = 1419033600L;

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "/2XHkh7xLqH168KRqMwDuiTmuyQi.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_FAVOURITE, true);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_MOVIE_ID, "13590");;
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORGINAL_LANGUAGE, "en");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Eddie Izzard: Glorious");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Eddie Izzard's routine has a loose trajectory from the beginning of the Old Testament and the creation of the world in seven days to Revelations. Along the way, we learn of the search for a career, bad giraffes, Prince Philip's gaffes, toilets in French campsites, the mysteries of hopscotch, becoming one's Dad and tranny bashing.");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/ub8d4LNtfwfibUclk89KDNQJx5k.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Eddie Izzard: Glorious");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 7.011709);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ADULT, false);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VIDEO, false);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 6.31);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 1000);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "1997-11-17");

        return movieValues;

    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
