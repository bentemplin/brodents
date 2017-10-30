package cs2340.gatech.edu.brodents;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by Benjamin Yarmowich on 10/15/2017.
 * Implemented by Rikesh Subedi on 10/16/2017
 */

public class ReportRatSightingActivity extends AppCompatActivity{

    // UI Containers
    private EditText etIncidentAddress;
    private EditText etIncidentZIP;
    private EditText etCityName;
    private EditText etCityBorough;
    private EditText etLongitude;
    private EditText etLatitude;
    private EditText etComplaintType;
    private EditText etLocationType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_sighting);

        // UI Logic here
        etIncidentAddress = (EditText) findViewById(R.id.etAddress);
        etIncidentZIP = (EditText) findViewById(R.id.etZIP);
        etCityName = (EditText) findViewById(R.id.etCityName);
        etCityBorough = (EditText) findViewById(R.id.etCityBorough);
        etLongitude = (EditText) findViewById(R.id.etLongitude);
        etLatitude = (EditText) findViewById(R.id.etLatitude);
        etComplaintType = (EditText) findViewById(R.id.etComplaintType);
        etLocationType = (EditText) findViewById(R.id.etLocationType);

        // TODO: make report button unclickable until all fields full


        Button btnReport = (Button) findViewById(R.id.btnReport);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(view -> finish());


        btnReport.setOnClickListener(view -> {
            //attemptRegistration();
            boolean success = submitReport();

            if (success) {
                // Data has already been submitted in isValidSubmit() method
                finish();
            }
        });



    }


    /**
     * Function attempts to submit report and returns the success of the operation.
     * @return the success of submitting the rat data
     */
    private boolean submitReport() {
        String complaintType = etComplaintType.getText().toString();
        String city = etCityName.getText().toString();
        String borough = etCityBorough.getText().toString();
        String address = etIncidentAddress.getText().toString();
        String locationType = etLocationType.getText().toString();

        // Problem children below
        int incidentZIP = Integer.parseInt(etIncidentZIP.getText().toString());
        double longitude = Double.parseDouble(etLongitude.getText().toString());
        double latitude = Double.parseDouble(etLatitude.getText().toString());

        if (complaintType.trim().isEmpty() || city.trim().isEmpty() ||
                locationType.trim().isEmpty() || borough.trim().isEmpty() ||
                address.trim().isEmpty() || (incidentZIP == 0) || (longitude == 0) ||
                (latitude == 0)) {
            // One of the fields is empty. Make specific if-statements later to specify message

            // Display a toast warning the user that a field is incomplete
            Context context = getApplicationContext();
            CharSequence submitText = "Must complete form! Some fields are incomplete.";
            int duration = Toast.LENGTH_LONG;

            Toast.makeText(context, submitText, duration).show();
            return false;
        }


        // TODO: check for valid data, ie. valid zip code


//        RatAppModel.getInstance().getSightingManager().insertSighting(complaintType, locationType,
//                incidentZIP, city, borough, address, latitude, longitude, null);

        AddSightingTask add = new AddSightingTask(address, complaintType, city, borough, locationType,
                incidentZIP, latitude, longitude);
        try {
            return add.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("In ReportRatSighting", e.getMessage(), e);
            return false;
        }
    }

    private final class AddSightingTask extends AsyncTask<Void, Void, Boolean> {
        final String addr;
        final String complaintType;
        final String city;
        final String borough;
        final String locType;
        final int zip;
        final double lat;
        final double longit;

        private AddSightingTask(String addr, String compType, String city, String borough,
            String locType, int zip, double lat, double longitude) {
            this.addr = addr;
            this.complaintType = compType;
            this.city = city;
            this.borough = borough;
            this.locType = locType;
            this.zip = zip;
            this.lat = lat;
            this.longit = longitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                RatAppModel.getInstance().getSightingManager().insertSighting(complaintType, locType,
                        zip, city, borough, addr, lat, longit);
                return true;
            } catch (Exception e) {
                Log.e("Add sighting", e.getMessage(), e);
                return false;
            }
        }
    }


}
