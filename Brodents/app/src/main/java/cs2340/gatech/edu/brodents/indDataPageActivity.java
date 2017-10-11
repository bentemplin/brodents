package cs2340.gatech.edu.brodents;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by Benjamin Yarmowich on 10/9/2017.
 */

public class indDataPageActivity extends AppCompatActivity {
    private RatSighting rat;
    private Layout layout;
    private TextView key;
    private TextView createdDate;
    private TextView locationType;
    private TextView incidentZip;
    private TextView incidentAddress;
    private TextView city;
    private TextView borough;
    private TextView lattitude;
    private TextView longitude;


    /**
     * Creates the Individual Rat Data Pages
     * @param savedInstanceState Current Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_rat_data);
        rat = RatSelected.getSelected(); //retrieves the selected rat

        //Links TextViews to the XML file
        key = (TextView)findViewById(R.id.key);
        createdDate = (TextView)findViewById(R.id.createdDate);
        locationType = (TextView)findViewById(R.id.locationType);
        incidentZip = (TextView)findViewById(R.id.incidentZip);
        incidentAddress = (TextView)findViewById(R.id.incidentAddress);
        city = (TextView)findViewById(R.id.city);
        borough = (TextView)findViewById(R.id.borough);
        lattitude = (TextView)findViewById(R.id.lattitude);
        longitude = (TextView)findViewById(R.id.longitude);

        //Date to String Formatter
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //Sets Texts Views to data from Selected Rat
        key.setText(Integer.toString(rat.getKey()));
        createdDate.setText(formatter.format(rat.getCreatedDate()));
        locationType.setText(rat.getLocationType());
        incidentAddress.setText(rat.getAddress());
        incidentZip.setText(Integer.toString(rat.getZip()));
        city.setText(rat.getCity());
        borough.setText(rat.getBorough());
        lattitude.setText(Double.toString(rat.getLatitude()));
        longitude.setText(Double.toString(rat.getLongitude()));
    }
}
