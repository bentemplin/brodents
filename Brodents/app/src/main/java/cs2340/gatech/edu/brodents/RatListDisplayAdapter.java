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

        public ViewHolder(View v, Activity a) {
            super(v);
            this.a = a;
            listenerRef = new WeakReference<>(listener);
            key = (TextView) v.findViewById(R.id.textView);

            v.setOnClickListener(this);
            key.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            Log.i("test",key.getText() + ", has been pressed");
            if (v.getId() == key.getId()) {
                Intent indDataPage = new Intent(a.getApplicationContext(), indDataPageActivity.class);
                a.startActivity(indDataPage);
            }
        }

        @Override
        public boolean onLongClick(View v){
            Log.i("test",v.toString() + "has been LOOOOONg pressed");
            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Hello Dialog")
                    .setMessage("LONG CLICK DIALOG WINDOW FOR ICON " + String.valueOf(getAdapterPosition()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.create().show();
            listenerRef.get().onLongClicked(getAdapterPosition());
            return true;
        }
    }

    public RatListDisplayAdapter(List<RatSighting> sightingList, Activity a, ClickListener listener) {
        this.sightingList = sightingList;
        this.listener = listener;
        this.parentActivity = a;
    }

    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.i("test", "new ViewHolder");
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v, parentActivity);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        RatSighting sighting = sightingList.get(position);
        Log.i("test", sighting.getKey() + ", key");
        holder.key.setText(Integer.toString(sighting.getKey()));
    }

    @Override
    public int getItemCount() {
        return sightingList.size();
    }
}
