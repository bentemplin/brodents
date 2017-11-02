package cs2340.gatech.edu.brodents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
/*
Main home page of the app
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Code for the report new sighting button
        Button report = findViewById(R.id.btnReport);
        report.setOnClickListener(view -> {
            // GET INTENT
            Intent sightingReport = new Intent(getApplicationContext(),
                    ReportRatSightingActivity.class);
            startActivity(sightingReport);
        });

        //Code for the Log out button
        Button mLogOut = findViewById(R.id.btnLogout);
        mLogOut.setOnClickListener(view -> {
            // INTENT NOT NEEDED, JUST POP THE ACTIVITY FROM THE STACK, login is parent
            RatAppModel.getInstance().clearCurrentUser();
            finish();
        });
        Button list = findViewById(R.id.btnList);
        list.setOnClickListener(view -> {
            // INTENT NOT NEEDED, JUST POP THE ACTIVITY FROM THE STACK, login is parent
            Intent listScreen = new Intent(getApplicationContext(), DataDisplayActivity.class);
            startActivity(listScreen);
        });
        Button mGraph = findViewById(R.id.btnGraph);
        mGraph.setOnClickListener(view -> {
            // INTENT NOT NEEDED, JUST POP THE ACTIVITY FROM THE STACK, login is parent
            Intent graph = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(graph);
        });
    }

}
