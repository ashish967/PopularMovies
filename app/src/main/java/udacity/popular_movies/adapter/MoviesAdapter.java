package udacity.popular_movies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import udacity.popular_movies.R;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.fragment.PopularMoviesFragment;
import udacity.popular_movies.utils.AppUtils;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public class MoviesAdapter extends CursorAdapter {


    public static final int layoutId=R.layout.movie_grid_item;
    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_FOOTER = 0;

    int mParentWidth;

    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    public static class MovieViewHolder{
        public ImageView mImageViewPoster;

        MovieViewHolder(View view){

            mImageViewPoster= (ImageView) view.findViewById(R.id.iv_posterview);
        }
    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
       /* switch (viewType) {
            case VIEW_TYPE_FOOTER: {
                layoutId = R.layout.footer_loader;
                break;
            }
           default: {
                layoutId = MoviesAdapter.layoutId;
                break;
            }
        }*/
        layoutId = MoviesAdapter.layoutId;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mParentWidth= parent.getWidth();

        MovieViewHolder viewHolder = new MovieViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        MovieViewHolder viewHolder = (MovieViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
           /* case VIEW_TYPE_FOOTER: {
                // Get weather icon
                break;
            }*/
            default: {
                if(viewHolder.mImageViewPoster.getLayoutParams()!=null){

                    int available_width= mParentWidth/2;
                    viewHolder.mImageViewPoster.getLayoutParams().width=available_width;
                    viewHolder.mImageViewPoster.getLayoutParams().height= (int) (available_width/0.68);
                }
                else{
                    Log.d(TAG,"Null viewholder image params");
                }


                PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(cursor.getString(PopularMoviesFragment.POSTER_PATH)))
                        .into(viewHolder.mImageViewPoster);

                break;
            }
        }
    }
}
