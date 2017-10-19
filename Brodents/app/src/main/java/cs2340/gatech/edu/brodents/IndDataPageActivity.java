package cs2340.gatech.edu.brodents;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;

import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Created by Benjamin Yarmowich on 10/9/2017.
 */

public class IndDataPageActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private RatSighting rat;
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
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");

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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.indMap);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("test", "Map attempt called");

        // Add where current rat sighting is nd move camera
        float zoom = 14f;
        LatLng sighting = new LatLng(rat.getLatitude(), rat.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sighting).title("Rat Sighting: #" + Integer.toString(rat.getKey())));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sighting,zoom));
    }
}
