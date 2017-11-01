package cs2340.gatech.edu.brodents;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import java.util.Locale;

/**
 * Created by Benjamin Yarmowich on 10/9/2017.
 */

public class IndDataPageActivity extends FragmentActivity implements OnMapReadyCallback {

    private RatSighting rat;


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
        TextView key = findViewById(R.id.key);
        TextView createdDate = findViewById(R.id.createdDate);
        TextView locationType = findViewById(R.id.locationType);
        TextView incidentZip = findViewById(R.id.incidentZip);
        TextView incidentAddress = findViewById(R.id.incidentAddress);
        TextView city = findViewById(R.id.city);
        TextView borough = findViewById(R.id.borough);
        TextView lattitude = findViewById(R.id.lattitude);
        TextView longitude = findViewById(R.id.longitude);

        //Date to String Formatter
        Format formatter = SimpleDateFormat.getDateInstance();

        //Sets Texts Views to data from Selected Rat
        key.setText(String.format(Locale.getDefault(), "%d", rat.getKey()));
        createdDate.setText(formatter.format(rat.getCreatedDate()));
        locationType.setText(rat.getLocationType());
        incidentAddress.setText(rat.getAddress());
        incidentZip.setText((String.format(Locale.getDefault(), "%d", rat.getZip())));
        city.setText(rat.getCity());
        borough.setText(rat.getBorough());
        lattitude.setText((String.format(Locale.getDefault(),"%f", rat.getLatitude())));
        longitude.setText((String.format(Locale.getDefault(), "%f", rat.getLongitude())));

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
        Log.i("test", "Map attempt called");

        // Add where current rat sighting is nd move camera
        float zoom = 14f;
        LatLng sighting = new LatLng(rat.getLatitude(), rat.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(sighting).title("Rat Sighting: #" +
                Integer.toString(rat.getKey())));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sighting,zoom));
    }
}
