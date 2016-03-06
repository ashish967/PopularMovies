package udacity.popular_movies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import udacity.popular_movies.R;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.datatypes.Movie;
import udacity.popular_movies.utils.AppUtils;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {


    public static final int layoutId=R.layout.movie_grid_item;
    private static final String TAG = MoviesAdapter.class.getSimpleName();

    public MoviesAdapter(Context context,List<Movie> objects){

        super(context, layoutId,objects);
    }

    private MoviesAdapter(Context context, int resource, List<Movie> objects) {
        super(context, resource, objects);
    }


    public static class MovieViewHolder{
        public ImageView mImageViewPoster;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView==null){

            convertView= View.inflate(getContext(), layoutId, null);
            MovieViewHolder viewHolder= new  MovieViewHolder();
            viewHolder.mImageViewPoster= (ImageView) convertView.findViewById(R.id.iv_posterview);
            convertView.setTag(viewHolder);
        }

        Movie movie= getItem(position);

        MovieViewHolder viewHolder= (MovieViewHolder) convertView.getTag();

        Log.d(TAG, "Movies : " + parent.getWidth() + "  covertview " + convertView.getWidth() + "  layoutparams " + convertView.getLayoutParams());


        if(viewHolder.mImageViewPoster.getLayoutParams()!=null){

            int available_width= parent.getWidth()/2;
            viewHolder.mImageViewPoster.getLayoutParams().width=available_width;
            viewHolder.mImageViewPoster.getLayoutParams().height= (int) (available_width/0.68);
        }
        else{
            Log.d(TAG,"Null viewholder image params");
        }


        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(movie.getPoster_path()))
        .into(viewHolder.mImageViewPoster);

        return convertView;
    }
}
