package cs2340.gatech.edu.brodents;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/5/2017.
 */

public class DataDisplayActivity extends Activity{
    private RecyclerView dataDisplay;
    private RecyclerView.Adapter displayAdapter;
    private RecyclerView.LayoutManager dataLayout;
    private List<RatSighting> ratData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataFetcher fetcher = new DataFetcher();
        try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results */
            ratData = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }
        setContentView(R.layout.rat_data_display);
            dataDisplay = (RecyclerView) findViewById(R.id.my_recycler_view);

            dataDisplay.setHasFixedSize(true);

            dataLayout = new LinearLayoutManager(this);
            dataDisplay.setLayoutManager(dataLayout);
            displayAdapter = new RatListDisplayAdapter(ratData);
            dataDisplay.setAdapter(displayAdapter);
    }

    /**
     * Need to use an AsyncTask to talk to the database, otherwise Android will kill the connection
     */
    private class DataFetcher extends AsyncTask<Void, Void, List<RatSighting>> {
        private RatSighting[] sightings;
        @Override
        protected ArrayList<RatSighting> doInBackground(Void... params) {
            ArrayList<RatSighting> list = new ArrayList<>();
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager man = model.getSightingManager();
            try {
                sightings = man.getNextBlock(10);
                for (RatSighting r : sightings) {
                    list.add(r);
                }
                return  list;
            } catch (SQLException e) {
                Log.e("SQL EXCEPTION", e.getMessage());
                return null;
            }
        }


    }


}
