package cs2340.gatech.edu.brodents;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

public class GraphActivity extends Activity {
    private RelativeLayout mainLayout;
    private LineChart mChart;
    private static List<RatSighting>  ratData;
    int lastRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastRow = 1;
        ratData = DataDisplayActivity.getRatData();

        /*lastUpdate = new Date();
        try {
            *//* The .get() function makes the function wait for the AsyncTask to finish and gets the
               results *//*
            ratData = fetcher.execute((Void) null).get();
        } catch (Exception e) {
            Log.e("DataDisplay", e.getMessage());
        }
*/
        //mainLayout.setContentView(R.layout.activity_graph);
        mainLayout = findViewById(R.id.graphLayout);
        mChart = new LineChart(this);
        Description title = new Description();
        title.setText("Rat Sightings Per Year");
        mChart.setDescription(title);
        mChart.setScaleEnabled(true);
        mChart.setDragEnabled(true);
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        addEntries();
        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.BLACK);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(Color.BLACK);
        y1.setDrawGridLines(true);
        y1.setAxisMaximum(100f);
        YAxis y2 = mChart.getAxisRight();
        y2.setEnabled(false);

    }
    protected void onResume() {
        super.onResume();
        ratData = DataDisplayActivity.getRatData();
        addEntries();
    }

    public void addEntries() {
        LineData data = mChart.getData();
        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            if (set == null ){
                set = createSet();
                data.addDataSet(set);
            }
        }
    }

    public LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "RatData");
        for (int i = 0; i < ratData.size(); i ++) {
            int x = ratData.get(i).getCreatedDate().getDate();
            int y = ratSightingsPerDate(ratData.get(i).getCreatedDate());
            Entry insert = new Entry(x,y);
            if (!set.contains(insert)) {
                set.addEntry(insert);
            }
            mChart.notifyDataSetChanged();
        }
        return set;
    }

    public int ratSightingsPerDate(Date date) {
        List<RatSighting> dates = new ArrayList<>();
        try {
            dates = RatSightingManager.getInstance().getSightingsBetween(date, date);
        } catch (Exception e) {
            Log.e("SQL EXCEPTION", "Bumped up");
        }
        return dates.size();
    }
    /**
     * Getter for the List of Rats
     * @return the current list of rats
     *//*
    public static List<RatSighting> getRatData() {
        return ratData;
    }

    *//**
     * Need to use an AsyncTask to talk to the database, otherwise Android will kill the connection
     *//*
    private class DataFetcher extends AsyncTask<Void, Void, List<RatSighting>> {
        private RatSighting[] sightings;

        @Override
        protected ArrayList<RatSighting> doInBackground(Void... params) {
            ArrayList<RatSighting> list = new ArrayList<>();
            RatAppModel.checkInitialization();
            RatAppModel model = RatAppModel.getInstance();
            RatSightingManager man = model.getSightingManager();
            try {
                sightings = man.getNextBlock(75, lastRow);
                for (RatSighting r : sightings) {
                    list.add(r);
                }
                lastRow += 75;
                return list;
            } catch (SQLException e) {
                Log.e("SQL EXCEPTION", e.getMessage());
                return null;
            }
        }
    }

        private class GetNewSightings2 extends AsyncTask<Void, Void, Object[]> {
            @Override
            protected Object[] doInBackground(Void... params) {
                RatAppModel.checkInitialization();
                RatSightingManager manager = RatAppModel.getInstance().getSightingManager();
                try {
                    return manager.getNewSightings(lastUpdate).toArray();
                } catch (NullPointerException e) {
                    Log.e("Get New NPE", e.getMessage(), e);
                    return null;
                }
            }
        }*/
    }





