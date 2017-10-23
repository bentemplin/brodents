package cs2340.gatech.edu.brodents;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<RatSighting> sightingList;
    private int lastRow = 1;
    private int endPointer;
    private String inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sightingList = DataDisplayActivity.getRatData();
        endPointer = 0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.wholeMap);
        mapFragment.getMapAsync(this);
        Button more = (Button) findViewById(R.id.more);
        more.setOnClickListener(view -> {
            mapFragment.getMapAsync(this);
        });

        Button btnGetByDate = (Button) findViewById(R.id.btnGetbyDate);
        btnGetByDate.setOnClickListener(view -> {
            String m_Text = "";
            //Make the alert box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Input Date");
            builder.setMessage("Put in the oldest date to display in the form MM-DD-YYYY");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            builder.setView(input);

            //Set up buttons in the alert box
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inputText = input.getText().toString();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        });
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
        LatLng cameraPointer = new LatLng(40.848057255, -73.914879001);
        int added = 0;
        while (added < 11) {
            if (!(endPointer < sightingList.size())) {
                break;
            } else if (sightingList.get(endPointer).getLongitude() < -60 && sightingList.get(endPointer).getLatitude() > 30) {
                // Add a marker at rat Sighting i and move the camera
                LatLng sightingPos = new LatLng(sightingList.get(endPointer).getLatitude(), sightingList.get(endPointer).getLongitude());
                mMap.addMarker(new MarkerOptions().position(sightingPos).title("Rat Sighting: " + sightingList.get(endPointer).getKey()));
                cameraPointer = sightingPos;
                added++;
            }
            endPointer++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPointer,10f));
    }
}
