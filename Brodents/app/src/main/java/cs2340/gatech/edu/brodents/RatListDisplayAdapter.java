package cs2340.gatech.edu.brodents;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
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
    private ClickListener listener;
    private Activity parentActivity;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView key;
        private WeakReference<ClickListener> listenerRef;
        private Activity a;

        /**
         * Creates a the View Holder for the Rat List Adapter
         * @param v the View that the View Holder will be editing
         * @param a The current DataDisplayActivity
         */
        public ViewHolder(View v, Activity a) {
            super(v);
            this.a = a;
            listenerRef = new WeakReference<>(listener);
            key = (TextView) v.findViewById(R.id.textView);

            v.setOnClickListener(this);
            key.setOnClickListener(this);
        }

        /**
         * Starts a new indDataPage when an id field is clicked
         * @param v current View
         */
        @Override
        public void onClick(View v){
            if (v.getId() == key.getId()) {
                new RatSelected(Integer.valueOf(getAdapterPosition()));
                Intent indDataPage = new Intent(a.getApplicationContext(), indDataPageActivity.class);
                a.startActivity(indDataPage);
            }
        }

        /**
         * Method is not used and is only implemented to follow an interface
         * @param v the current view
         * @return true
         */
        @Override
        public boolean onLongClick(View v){
            return true;
        }
    }

    /**
     * Instatiates the DisplayAdapter
     * @param sightingList the list of Rat Sightings to be displayed
     * @param a The current DataDisplayActivity to pass onto the View Adapter
     * @param listener A ClickListener used to accept clicks
     */
    public RatListDisplayAdapter(List<RatSighting> sightingList, Activity a, ClickListener listener) {
        this.sightingList = sightingList;
        this.listener = listener;
        this.parentActivity = a;
    }

    /**
     * Creates the View Holder
     * @param parent the ViewGroup the RecyclerView is stored in
     * @param viewType the int type
     * @return the generated ViewHolder
     */
    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v, parentActivity);
        return vh;
    }

    /**
     * Replaces the textView text with the Rat Sighting ID
     * @param holder the ViewHolder containing the TextView
     * @param position the position of the rat in the array
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RatSighting sighting = sightingList.get(position);
        holder.key.setText(Integer.toString(sighting.getKey()));
    }

    /**
     * @return the size of the sightingList
     */
    @Override
    public int getItemCount() {
        return sightingList.size();
    }
}
