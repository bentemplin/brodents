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

import java.sql.SQLException;
import java.util.ArrayList;
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

    /**
     * Starts the Data Display Activity
     * @param savedInstanceState the current Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fetched the Rat Data from the Data Base
        fetcher = new DataFetcher();
        try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results */
            ratData = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }

        //Sets the view to the XML file rat_data_display
        setContentView(R.layout.rat_data_display);
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

        Button mLogOut = (Button) findViewById(R.id.btnLogout);
        mLogOut.setText("Logout");
        mLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GET INTENT
                RatAppModel.getInstance().clearCurrentUser();
                Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginScreen);
            }

        });
        Button report = (Button) findViewById(R.id.btnReport);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // GET INTENT
                Intent sightingReport = new Intent(getApplicationContext(), ReportRatSightingActivity.class);
                startActivity(sightingReport);
            }
        });
    }
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
                sightings = man.getNextBlock(100);
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
