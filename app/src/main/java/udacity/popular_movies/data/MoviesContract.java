package udacity.popular_movies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ashish-novelroots on 2/4/16.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY="com.example.udacity.popular_movies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final String PATH_FAVORITE_MOVIES="fav_movies";
    public static final class FavoriteMovies implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE_MOVIES;


        public static final String TABLE_NAME = "fav_movies";

        public static final String COLUMN_MOVIE_ID="movie_id";

        public static final String COLUMN_IS_FAVORITE="is_favorite";


        public static Uri buidFavMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getFavMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


    }

    public static final class MovieEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_POSTER_PATH="poster_path";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_MOVIE_ID="id";
        public static final String COLUMN_ORIGINAL_TITLE="orginal_title";
        public static final String COLUMN_ORGINAL_LANGUAGE="original_language";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_BACKDROP_PATH="backdrop_path";
        public static final String COLUMN_FAVOURITE="favourite";
        public static final String COLUMN_POPULARITY="popularity";
        public static final String COLUMN_VOTE_AVERAGE="vote_avg";
        public static final String COLUMN_VOTE_COUNT="vote_count";
        public static final String COLUMN_ADULT="adult";
        public static final String COLUMN_VIDEO="video";
        public static final String COLUMN_RELEASE_DATE="release_date";


        public static Uri buidMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
