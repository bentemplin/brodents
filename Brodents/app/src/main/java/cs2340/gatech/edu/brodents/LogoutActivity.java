package cs2340.gatech.edu.brodents;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mLogOut = (Button) findViewById(R.id.btnLogout);
        mLogOut.setText("Logout");
        mLogOut.setOnClickListener(view -> {
            // GET INTENT
            RatAppModel.getInstance().clearCurrentUser();
            Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginScreen);
        });
    }



}
