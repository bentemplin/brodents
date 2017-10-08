package cs2340.gatech.edu.brodents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Benjamin Yarmowich on 10/6/2017.
 */

public class RatListDisplayAdapter extends
        RecyclerView.Adapter<RatListDisplayAdapter.ViewHolder>{
    private String[] ratData;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public RatListDisplayAdapter(String[] data ) {
        ratData = data;
    }

    @Override
    public  RatListDisplayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(ratData[position]);
    }

    @Override
    public int getItemCount() {
        return ratData.length;
    }
}
