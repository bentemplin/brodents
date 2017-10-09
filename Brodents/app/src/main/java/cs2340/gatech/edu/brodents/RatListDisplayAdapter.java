package cs2340.gatech.edu.brodents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/6/2017.
 */

/*
 * This class pretty much just follows an online guide I found for making recycler views...I'm not
 * entirely sure what a lot of the stuff is used for. -Ben Templin
 */
public class RatListDisplayAdapter extends
        RecyclerView.Adapter<RatListDisplayAdapter.ViewHolder>{

    private List<RatSighting> sightingList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView key;
        public ViewHolder(View v) {
            super(v);
            key = (TextView) v.findViewById(R.id.textView2);
        }
    }

    public RatListDisplayAdapter(List<RatSighting> sightingList) {
        this.sightingList = sightingList;
    }

    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RatSighting sighting = sightingList.get(position);
        holder.key.setText(Integer.toString(sighting.getKey()));
    }

    @Override
    public int getItemCount() {
        return sightingList.size();
    }
}
