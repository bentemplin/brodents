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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private List<RatSighting> sightingList;
    private List<RatSighting> displayList;
    private int lastRow;
    private String inputTextStart;
    private String inputTextEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        displayList = new ArrayList<>();
        sightingList = DataDisplayActivity.getRatData();
        populateList(0, 10);
        lastRow = 10;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.wholeMap);
        mapFragment.getMapAsync(this);
        Button more = (Button) findViewById(R.id.more);
        more.setOnClickListener(view -> {
            try {
                populateList(lastRow, 10);
                lastRow += 10;
            } catch (NullPointerException e) {
                displayList = new ArrayList<RatSighting>();
                lastRow += 10;
                populateList(0, lastRow);
            }
            mapFragment.getMapAsync(this);
        });

        Button btnGetByDate = (Button) findViewById(R.id.btnGetbyDate);
        btnGetByDate.setOnClickListener(view -> {
            //Make the alert box
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Input Date");
            builder.setMessage("Put in the oldest date and the newest date " +
                    "to display");
            final EditText inputStart = new EditText(this);
            final EditText inputEnd = new EditText(this);
            inputStart.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            inputEnd.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(inputStart);
            layout.addView(inputEnd);
            builder.setView(layout);
            //Set up buttons in the alert box
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    inputTextStart = inputStart.getText().toString();
                    inputTextEnd = inputEnd.getText().toString();
                    String datePattern = "MM-dd-yyyy";
                    SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
                    try {
                        Date startDateRaw = formatter.parse(inputTextStart);
                        Date endDateRaw = formatter.parse(inputTextEnd);
                        Date earliest = formatter.parse("01-01-2010");
                        if (validateRange(startDateRaw, endDateRaw, earliest)) {
                            GetTimedSightings sightingFetcher = new GetTimedSightings();
                            displayList = new ArrayList<RatSighting>();
                            displayList = sightingFetcher.execute(startDateRaw, endDateRaw).get();
                        } else {
                            return;
                        }
                    } catch (ParseException e) {
                        // TODO Put a toast here
                        Log.e("InputClick", e.getMessage(), e);
                        displayAlert("Invalid Date", "Input date in format MM-DD-YYYY");
                        displayList = new ArrayList<RatSighting>();
                    } catch (Exception e) {
                        Log.e("InputClick", e.getMessage(), e);
                    }
                    mapFragment.getMapAsync(MapsActivity.this);
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
        LatLng cameraPointer = new LatLng(40.7800077,-73.9278835);
        int added = 0;
        while (displayList!=null && added < displayList.size()) {
                // Add a marker at rat Sighting i and move the camera
                LatLng sightingPos = new LatLng(displayList.get(added).getLatitude(), displayList.get(added).getLongitude());
                mMap.addMarker(new MarkerOptions().position(sightingPos).title("Rat Sighting: " + displayList.get(added).getKey()).snippet("Click here for more info"));
                added++;
            }
        if (displayList == null) {
            //This means no sightings found, so clear the markers
            mMap.clear();
            //Raise an alert to let the user know there are no sightings for this period.
            displayAlert("No Sightings Found", "There are no sightings for this date range");

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPointer,11f));
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        char id = marker.getId().charAt(1);
        int index = id - 48;
        new RatSelected(displayList.get(index));
        Intent indRatSighting = new Intent(getApplicationContext(), IndDataPageActivity.class);
        startActivity(indRatSighting);
    }

    private void populateList(int start, int size) {
        int added = 0;
        int index = start;
        while (added <= size && index < sightingList.size()) {
            if (sightingList.get(index).getLongitude() < -60 && sightingList.get(index).getLatitude() > 30) {
                displayList.add(sightingList.get(index));
                added++;
            }
            index++;
        }
    }

    private class GetTimedSightings extends AsyncTask<Date, Void, List<RatSighting>> {
        @Override
        protected  ArrayList<RatSighting> doInBackground(Date... params) {
            try {
                return RatAppModel.getInstance().getSightingManager().getSightingsBetween(params[0],
                        params[1]);
            } catch (SQLException e) {
                Log.e("GetTimedSightings", e.getMessage(), e);
                return null;
            }
        }
    }

    private void displayAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private boolean validateRange(Date start, Date end, Date earliest) {
        Date now = new Date();
        if (start.after(end) || start.after(now) || end.after(now)) {
            displayAlert("Invalid Range", "Please enter dates in the format MM-DD-YYYY and that " +
                    "are not in the future");
            return false;
        } else if (end.before(earliest)) {
            displayAlert("Warning", "The earliest sighting is on 01-01-2010. Your date range ends " +
                    "before that.");
            return false;
        } else {
            return true;
        }
    }
}
