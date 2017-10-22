package cs2340.gatech.edu.brodents;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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
    private DataFetcher fetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        fetcher = new DataFetcher();
        endPointer = 0;
        try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results */
            sightingList = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.wholeMap);
        mapFragment.getMapAsync(this);
        Button more = (Button) findViewById(R.id.btnMap);
//        more.setOnClickListener(view -> {
//            try {
//            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
//               results */
//                sightingList = fetcher.execute((Void) null).get();
//            } catch (Exception e) {
//                Log.e("DataDisplay", e.getMessage());
//            }
//        });;
    }

    private class DataFetcher extends AsyncTask<Void, Void, List<RatSighting>> {
        private RatSighting[] sightings;
        @Override
        protected ArrayList<RatSighting> doInBackground(Void... params) {
            ArrayList<RatSighting> list = new ArrayList<>();
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager man = model.getSightingManager();
            try {
                sightings = man.getNextBlock(15, lastRow);
                for (RatSighting r : sightings) {
                    list.add(r);
                }
                lastRow += 15;
                return  list;
            } catch (SQLException e) {
                Log.e("SQL EXCEPTION", e.getMessage());
                return null;
            }
        }
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
        while (added < 10){
            try {
                if (sightingList.get(endPointer).getLongitude() < -60 && sightingList.get(endPointer).getLatitude() > 30) {
                    // Add a marker at rat Sighting i and move the camera
                    LatLng sightingPos = new LatLng(sightingList.get(endPointer).getLatitude(), sightingList.get(endPointer).getLongitude());
                    mMap.addMarker(new MarkerOptions().position(sightingPos).title("Rat Sighting: " + sightingList.get(endPointer).getKey()));
                    cameraPointer = sightingPos;
                    added++;
                }
                endPointer++;
            } catch (IndexOutOfBoundsException e){
                try {
            /* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results */
                    sightingList = fetcher.execute((Void) null).get();
                } catch (Exception b) {
                    Log.e("DataDisplay", b.getMessage());
                }
            }
        }
        int start = 0;
        while (sightingList.get(endPointer).getLongitude() > -60 && sightingList.get(endPointer).getLatitude() < 30) {
            start++;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPointer,10f));
    }
}
