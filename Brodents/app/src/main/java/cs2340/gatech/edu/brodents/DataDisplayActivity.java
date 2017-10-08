package cs2340.gatech.edu.brodents;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Benjamin Yarmowich on 10/5/2017.
 */

public class DataDisplayActivity extends Activity{
    private RecyclerView dataDisplay;
    private RecyclerView.Adapter displayAdapter;
    private RecyclerView.LayoutManager dataLayout;
    private String[] ratData = {"0","Hello","fuckDuck"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rat_data_display);

        dataDisplay.setHasFixedSize(true);

        dataLayout = new LinearLayoutManager(this);
        dataDisplay.setLayoutManager(dataLayout);

        displayAdapter = new RatListDisplayAdapter(ratData);
        dataDisplay.setAdapter(displayAdapter);
    }


}
