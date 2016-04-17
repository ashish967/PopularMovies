package udacity.popular_movies.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import udacity.popular_movies.R;
import udacity.popular_movies.datatypes.ReviewType;

public class ReviewsAdapter extends RecyclerViewHeaderFooterAdapter {



    public ReviewsAdapter(AppCompatActivity activity,ArrayList<ReviewType> reviews){
        mActivity = activity;
        mDataSet= reviews;
    }



    public static class UserReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView mTvUserName,mTvContent,mTvMoreInfo;
        Activity mActivity;
        ReviewType mReview;
        public UserReviewHolder(View itemview, Activity mActivity) {
            super(itemview);


            this.mActivity= mActivity;
            mTvUserName= (TextView) itemview.findViewById(R.id.tv_author_name);
            mTvContent= (TextView) itemview.findViewById(R.id.tv_content);
            mTvMoreInfo= (TextView) itemview.findViewById(R.id.tv_more_info);

            mTvMoreInfo.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.tv_more_info:

                    openMoreInfo();
                    break;

            }

        }

        private void openMoreInfo() {

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mReview.getUrl()));

            mActivity.startActivity(new Intent(Intent.createChooser(intent, "More info")));
        }




        public void bindView(ReviewType reviewType){

            mReview =reviewType;




            mTvUserName.setText(mReview.getAuthor());


            mTvContent.setText(mReview.getContent());

        }

    }
    @Override
    protected void bindView(RecyclerView.ViewHolder holder, int position) {


        UserReviewHolder reviewHolder= (UserReviewHolder) holder;

        reviewHolder.bindView((ReviewType) mDataSet.get(position));


    }

    @Override
    protected RecyclerView.ViewHolder createObjectView(ViewGroup parent) {


        View itemview= LayoutInflater.from(mActivity).inflate(R.layout.user_reviews_item_layout,parent,false);
        return new UserReviewHolder(itemview,mActivity);
    }
}
