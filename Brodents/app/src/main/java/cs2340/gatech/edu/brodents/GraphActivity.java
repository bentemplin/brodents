package cs2340.gatech.edu.brodents;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Benjamin Yarmowich on 10/18/2017.
 */

public class GraphActivity extends AppCompatActivity{
    /**
     * Starts the Data Display Activity
     * @param savedInstanceState the current Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        finish();
    }
}
