package udacity.popular_movies.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import udacity.popular_movies.R;
import udacity.popular_movies.application.PopularMovieApplication;
import udacity.popular_movies.datatypes.RealmMovie;
import udacity.popular_movies.utils.AppUtils;

/**
 * Created by ashish-novelroots on 6/3/16.
 */
public class MoviesAdapter extends RealmBaseAdapter<RealmMovie> implements ListAdapter {


    public static final int layoutId=R.layout.movie_grid_item;
    private static final String TAG = MoviesAdapter.class.getSimpleName();

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_FOOTER = 0;

    int mParentWidth;

    public MoviesAdapter(Context context,
                         RealmResults<RealmMovie> realmResults,
                         boolean automaticUpdate)
    {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(layoutId, parent, false);
            mParentWidth= parent.getWidth();
            MovieViewHolder viewHolder = new MovieViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        MovieViewHolder viewHolder = (MovieViewHolder) convertView.getTag();
        if(viewHolder.mImageViewPoster.getLayoutParams()!=null){

            int available_width= mParentWidth/2;
            viewHolder.mImageViewPoster.getLayoutParams().width=available_width;
            viewHolder.mImageViewPoster.getLayoutParams().height= (int) (available_width/0.68);
        }
        else{
            Log.d(TAG,"Null viewholder image params");
        }

        RealmMovie item= realmResults.get(position);
        PopularMovieApplication.mPicasso.load(AppUtils.createPosterUrl(item.getPoster_path()))
                .into(viewHolder.mImageViewPoster);


        return convertView;

    }


    public static class MovieViewHolder{
        public ImageView mImageViewPoster;

        MovieViewHolder(View view){

            mImageViewPoster= (ImageView) view.findViewById(R.id.iv_posterview);
        }
    }




}
