package udacity.popular_movies.adapter;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract  class RecyclerViewHeaderFooterAdapter extends RecyclerView.Adapter {




    ArrayList mDataSet;
    AppCompatActivity mActivity;

    int TYPE_HEADER=0,TYPE_FOOTER=1,TYPE_ITEM=2;
    View mHeader,mFooter;

    public void setHeader(View header){

        mHeader=header;
    }

    public void setFooter(View footer){

        mFooter= footer;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER)
        {

            return  new VHHeader(mHeader);
        }
        else if(viewType == TYPE_FOOTER)
        {
            return new VHFooter(mFooter);
        }
        else{

            return  createObjectView(parent);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if((holder instanceof VHHeader) || (holder instanceof  VHFooter)||isPositionFooter(position)|| isPositionHeader(position)){
            return;
        }



        if(mHeader!=null){
            position=position-1;
        }

        bindView(holder,position);
    }

    protected abstract void bindView(RecyclerView.ViewHolder holder, int position);

    protected abstract RecyclerView.ViewHolder createObjectView(ViewGroup parent);


    @Override
    public int getItemCount() {
        return mDataSet.size()+(mHeader==null?0:1)+(mFooter==null?0:1);

    }


    class VHHeader extends RecyclerView.ViewHolder{
        public VHHeader(View itemView) {
            super(itemView);
        }
    }

    class VHFooter extends RecyclerView.ViewHolder{

        public VHFooter(View itemView) {
            super(itemView);

        }
    }
    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;
        if(isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;

    }

    public boolean isPositionHeader(int position)
    {
        return position == 0&&mHeader!=null;
    }
    public boolean isPositionFooter(int position)
    {
        int count=getItemCount();
        return (position == (count-1))&&mFooter!=null;
    }
}
