package cs2340.gatech.edu.brodents;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/5/2017.
 */

public class DataDisplayActivity extends AppCompatActivity {
    private RecyclerView dataDisplay;
    private RecyclerView.Adapter displayAdapter;
    private static List<RatSighting> ratData;
    private SearchFetcher searchFetch;
    private static int key;
    private static int lastRow;
    private static Date lastUpdate;

    private static final int MIN_KEY = 10000000;
    private static final int MAX_KEY = 40000000;
    private static final int DEFAULT_BLOCK_SIZE = 75;

    /**
     * Starts the Data Display Activity
     * @param savedInstanceState the current Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fetched the Rat Data from the Data Base
        lastRow = 1;
        DataFetcher fetcher = new DataFetcher();
        lastUpdate = new Date();
        try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results. */
            ratData = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }

        //Sets the view to the XML file rat_data_display
        setContentView(R.layout.rat_data_display);

        //Builds the Recycler View
        dataDisplay = findViewById(R.id.my_recycler_view);

        dataDisplay.setHasFixedSize(true);

        RecyclerView.LayoutManager dataLayout = new LinearLayoutManager(this);
        dataDisplay.setLayoutManager(dataLayout);
        displayAdapter = new RatListDisplayAdapter(ratData, this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                //callback performed on click
            }
            @Override
            public void onLongClicked(int position) {
                //callback performed on click
            }
        });
        dataDisplay.setAdapter(displayAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Code for Search Bar
        EditText searchBar = findViewById(R.id.searchText);
        Button searchBtn = findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(view -> {
            key = Integer.parseInt(searchBar.getText().toString());
            Log.i("text", "key selected: " + key);
            if ((key < MIN_KEY) || (key > MAX_KEY)) {
                searchBar.setError("valid keys are between 10000000 and 40000000");
            } else {
                searchFetch = new SearchFetcher();
                try {
                    RatSelected.setRatSelected(searchFetch.execute((Void) null).get());
                    Log.i("text", "Rat selected: " +
                            RatSelected.getSelected().toString());
                } catch (Exception e) {
                    Log.e("SQL EXCEPTION", "Bumped up");
                }
                if (RatSelected.getSelected() != null) {
                    Intent indRatSighting = new Intent(getApplicationContext(),
                            IndDataPageActivity.class);
                    startActivity(indRatSighting);
                } else {
                    searchBar.setError("The key you have entered cannot be found");
                }
            }

        });
        Button mMaps = findViewById(R.id.btnMap);
        mMaps.setOnClickListener(view -> {
            // INTENT NOT NEEDED, JUST POP THE ACTIVITY FROM THE STACK, login is parent
            Intent map = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(map);
        });    }

    @Override
    protected void onResume() {
        super.onResume();
        GetNewSightings newSightings = new GetNewSightings();
        try {
            RatSighting[] newAdds = (RatSighting[]) newSightings.execute((Void) null).get();
            if (newAdds != null) {
                for (RatSighting r : newAdds) {
                    /*
                        Only add the new sighting if it isn't already in the list
                     */
                    if (!ratData.contains(r)) {
                        ratData.add(0, r);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage(), e);
        }
        displayAdapter = new RatListDisplayAdapter(ratData, this, new ClickListener() {
            @Override
            public void onPositionClicked(int position) {
                //callback performed on click
            }
            @Override
            public void onLongClicked(int position) {
                //callback performed on click
            }
        });
        dataDisplay.setAdapter(displayAdapter);
    }

    /**
     * Getter for the List of Rats
     * @return the current list of rats
     */
    public static List<RatSighting> getRatData() {
        return ratData;
    }

    /**
     * Need to use an AsyncTask to talk to the database, otherwise Android will kill the connection
     */
    private static class DataFetcher extends AsyncTask<Void, Void, List<RatSighting>> {
        private RatSighting[] sightings;
        @Override
        protected ArrayList<RatSighting> doInBackground(Void... params) {
            ArrayList<RatSighting> list = new ArrayList<>();
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager man = model.getSightingManager();
            try {
                sightings = man.getNextBlock(DEFAULT_BLOCK_SIZE, lastRow);
                Collections.addAll(list, sightings);
                lastRow += DEFAULT_BLOCK_SIZE;
                return  list;
            } catch (SQLException e) {
                Log.e("SQL EXCEPTION", e.getMessage());
                return null;
            }
        }
    }

    private static class SearchFetcher extends AsyncTask<Void, Void, RatSighting> {
        @Override
        protected RatSighting doInBackground(Void... params) {
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager manager = model.getSightingManager();
            try {
                Log.i("text", "Access: " +key + ": " + manager.getSighting(key));
                return manager.getSighting(key);
            } catch (NullPointerException e) {
                Log.e("Search Fetch NPE", e.getMessage());
                return null;
            }
        }
    }

    private static class GetNewSightings extends AsyncTask<Void, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Void... params) {
            RatAppModel.checkInitialization();
            RatSightingManager manager = RatAppModel.getInstance().getSightingManager();
            try {

                List<RatSighting> newSightings = manager.getNewSightings(lastUpdate);
                if ((newSightings != null) && (!newSightings.isEmpty())) {
                    return newSightings.toArray();
                } else {
                    return null;
                }
            } catch (NullPointerException e) {
                Log.e("Get New NPE", e.getMessage(), e);
                return null;
            }
        }
    }

}
