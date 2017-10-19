package cs2340.gatech.edu.brodents;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/5/2017.
 */

public class DataDisplayActivity extends AppCompatActivity {
    private RecyclerView dataDisplay;
    private RecyclerView.Adapter displayAdapter;
    private RecyclerView.LayoutManager dataLayout;
    private static List<RatSighting> ratData;
    private DataFetcher fetcher;
    private SearchFetcher searchFetch;
    protected int key;
    private int lastRow;
    private Date lastUpdate;

    /**
     * Starts the Data Display Activity
     * @param savedInstanceState the current Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fetched the Rat Data from the Data Base
        lastRow = 1;
        fetcher = new DataFetcher();
        lastUpdate = new Date();
        try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results */
            ratData = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }

        //Sets the view to the XML file rat_data_display
        setContentView(R.layout.rat_data_display);

        //Builds the Recycler View
        dataDisplay = (RecyclerView) findViewById(R.id.my_recycler_view);

        dataDisplay.setHasFixedSize(true);

        dataLayout = new LinearLayoutManager(this);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Code for Search Bar
        EditText searchBar = (EditText) findViewById(R.id.searchText);
        Button searchBtn = (Button) findViewById(R.id.btnSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = Integer.parseInt(searchBar.getText().toString());
                Log.i("text", "key selected: " + key);
                if (key < 11400000 || key > 38000000) {
                    searchBar.setError("valid keys are between 10000000 and 40000000");
                } else {
                    searchFetch = new SearchFetcher();
                    try {
                        new RatSelected(searchFetch.execute((Void) null).get());
                        Log.i("text", "Rat selected: " + RatSelected.getSelected().toString());
                    } catch (Exception e) {
                        Log.e("SQL EXCEPTION", "Bumped up");
                    }
                    if (RatSelected.getSelected() != null) {
                        Intent indRatSighting = new Intent(getApplicationContext(), IndDataPageActivity.class);
                        startActivity(indRatSighting);
                    } else {
                        searchBar.setError("The key you have entered cannot be found");
                    }
                }
            }
        });

        //Code for the report new sighting button
        Button report = (Button) findViewById(R.id.btnReport);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GET INTENT
                Intent sightingReport = new Intent(getApplicationContext(), ReportRatSightingActivity.class);
                startActivity(sightingReport);
            }
        });

        //Code for the Log out button
        Button mLogOut = (Button) findViewById(R.id.btnLogout);
        mLogOut.setText("Logout");
        mLogOut.setOnClickListener(view -> {
            // INTENT NOT NEEDED, JUST POP THE ACTIVITY FROM THE STACK, login is parent
            RatAppModel.getInstance().clearCurrentUser();
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetNewSightings newSightings = new GetNewSightings();
        try {
            Object[] newAdds = newSightings.execute((Void) null).get();
            for (Object r : newAdds) {
                if (!ratData.contains(r)) {
                    ratData.add(0, (RatSighting) r);
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
    private class DataFetcher extends AsyncTask<Void, Void, List<RatSighting>> {
        private RatSighting[] sightings;
        @Override
        protected ArrayList<RatSighting> doInBackground(Void... params) {
            ArrayList<RatSighting> list = new ArrayList<>();
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager man = model.getSightingManager();
            try {
                sightings = man.getNextBlock(75, lastRow);
                for (RatSighting r : sightings) {
                    list.add(r);
                }
                lastRow += 75;
                return  list;
            } catch (SQLException e) {
                Log.e("SQL EXCEPTION", e.getMessage());
                return null;
            }
        }
    }

    private class SearchFetcher extends AsyncTask<Void, Void, RatSighting> {
        @Override
        protected RatSighting doInBackground(Void... params) {
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager manager = model.getSightingManager();
            try {
                Log.i("text", "Access: " +key + ": " + manager.getSighting(key));
                return manager.getSighting(key);
            } catch (Exception e) {
                Log.e("Search Fetch EXCEPTION", e.getMessage());
                return null;
            }
        }
    }

    private class GetNewSightings extends AsyncTask<Void, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Void... params) {
            RatAppModel.checkInitialization();
            RatSightingManager manager = RatAppModel.getInstance().getSightingManager();
            try {
                return manager.getNewSightings(lastUpdate).toArray();
            } catch (Exception e) {
                Log.e("Get New Sighting", e.getMessage(), e);
                return null;
            }
        }
    }

}
