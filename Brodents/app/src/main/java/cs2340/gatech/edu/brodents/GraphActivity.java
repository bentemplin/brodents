package cs2340.gatech.edu.brodents;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.sql.SQLException;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

/**
 * @Author Benjamin Yarmowich, Nikolai Vorobiev, Ben Templin
 * @Version 1.0
 */

public class GraphActivity extends Activity {
    private LineChart mChart;
    private GraphDataFetcher dataFetcher;
    private LineData data;
    private static final float DIVISOR = 86400000; //This makes the change in x-axis one day
    private static final float TEXT_SIZE = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_graph);
            mChart = findViewById(R.id.graphLayout);
            Description title = new Description();
            title.setText("Rat Sightings per Day");
            title.setTextSize(TEXT_SIZE);
            mChart.setDescription(title);
            mChart.getLegend().setEnabled(false);
            mChart.setScaleEnabled(true);
            mChart.setDragEnabled(true);
            dataFetcher = null;
            data = null;
            Button getGraph = findViewById(R.id.btnGetGraph);
            title.setTextAlign(Paint.Align.CENTER);
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            title.setPosition(screenWidth/2, mChart.getTop()+TEXT_SIZE*3);
            getGraph.setOnClickListener((View view) -> getDataGraph());
        } catch (Exception e) {
            Log.e("GraphActivity", e.getMessage(), e);
        }
    }

    private class GraphDataFetcher extends AsyncTask<Void, Void, LineDataSet> {
        @SuppressWarnings("CanBeFinal")
        private Date start;
        @SuppressWarnings("CanBeFinal")
        private Date end;

        GraphDataFetcher(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected LineDataSet doInBackground(Void... params) {
            String qText = "SELECT COUNT(*), DATE(createdDate) FROM " +
                    "sightingInfo WHERE " +
                    "createdDate >= ? AND createdDate <= ? GROUP BY DATE(createdDate);";
            DatabaseConnector connector = RatAppModel.getInstance().getConnector();
            try {
                PreparedStatement stmt = connector.getStatement(qText);
                Timestamp startStamp = new Timestamp(start.getTime());
                Timestamp endStamp = new Timestamp(end.getTime());
                stmt.setTimestamp(1, startStamp);
                stmt.setTimestamp(2, endStamp);
                ResultSet results = connector.query(stmt);
                List<Entry> entries = new ArrayList<>();
                long targetDay;
                int count;
                long firstDay = 0;
                while (results.next()) {
                    targetDay = results.getTimestamp("DATE(createdDate)").getTime();
                    count = results.getInt("COUNT(*)");
                    if (firstDay == 0) {
                        firstDay = targetDay;
                    }
                    entries.add(new Entry(((targetDay-firstDay)/DIVISOR) , (float) count));
                }
                results.close();
                Collections.sort(entries, new EntryXComparator());
                return new LineDataSet(entries, "Sightings v. Time");
            } catch (SQLException e) {
                Log.e("GraphDataFetcher", e.getMessage(), e);
                return null;
            }
        }
    }

    private void getDataGraph() {
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
        builder.setPositiveButton("OK", (dialog, which) -> {
            String inputTextStart = inputStart.getText().toString();
            String inputTextEnd = inputEnd.getText().toString();
            String datePattern = "MM-dd-yyyy";
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            try {
                Date startDateRaw = formatter.parse(inputTextStart);
                Date endDateRaw = formatter.parse(inputTextEnd);
                Date earliest = formatter.parse("01-01-2010");
                if (validateRange(startDateRaw, endDateRaw, earliest)) {
                    dataFetcher = new GraphDataFetcher(startDateRaw, endDateRaw);
                } else {
                    dataFetcher = null;
                    return;
                }
            } catch (ParseException e) {
                Log.e("InputClick", e.getMessage(), e);
                displayAlert("Invalid Date", "Input date in format MM-DD-YYYY");
                dataFetcher = null;
            } catch (Exception e) {
                Log.e("InputClick", e.getMessage(), e);
                dataFetcher = null;
            }
            if (dataFetcher == null) {
                getDataGraph();
            }
            LineDataSet dataSet;
            try {
                dataSet = dataFetcher.execute((Void) null).get();
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                data = new LineData(dataSet);
                displayData();
                Log.i("getDataGraph", "At end of method!");
            } catch (Exception e) {
                Log.e("GraphActivity", e.getMessage(), e);
            }
        });
        builder.show();
    }

    private boolean validateRange(Date start, Date end, Date earliest) {
        Date now = new Date();
        if (start.after(end) || start.after(now) || end.after(now)) {
            displayAlert("Invalid Range", "Please enter dates in the format MM-DD-YYYY and that " +
                    "are not in the future");
            return false;
        } else if (end.before(earliest)) {
            displayAlert("Warning", "The earliest sighting is on 01-01-2010." + "" +
                    "Your date range ends before that.");
            return false;
        } else {
            return true;
        }
    }

    private void displayAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    /**
     * This method will display whatever is in the data field on the graph.
     * To use, update the instance variable data and displayData will take care of the rest.
     */
    private void displayData() {
        if (data != null) {
            mChart.setData(data);
            XAxis x1 = mChart.getXAxis();
            x1.setTextColor(Color.BLACK);
            x1.setDrawGridLines(false);
            x1.setAvoidFirstLastClipping(true);
            x1.setPosition(XAxis.XAxisPosition.BOTTOM);
            x1.resetAxisMaximum();
            x1.resetAxisMinimum();

            YAxis y1 = mChart.getAxisLeft();
            y1.setTextColor(Color.BLACK);
            y1.setDrawGridLines(true);
            y1.setAxisMinimum(0f);
            YAxis y2 = mChart.getAxisRight();
            y2.setEnabled(false);

            mChart.invalidate();
        }
    }
}





