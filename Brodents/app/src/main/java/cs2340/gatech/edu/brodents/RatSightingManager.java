package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * This class will interface the Brodents app with the database of rat sightings. It is designed in
 * a singleton format.
 *
 * @author Ben Templin
 * @version 1.0
 */

public class RatSightingManager {
    private DatabaseConnector db;
    private boolean isInit = false;
    private int lastRow;
    private static RatSightingManager instance = null;
    private static final String TAG = "RatSightingManager";

    private RatSightingManager(DatabaseConnector connector) {
        db = connector;
        lastRow = 1;
    }

    /**
     * This method initializes the RatSightingManager
     * @param dbConnector DatabaseConnector that links the RatSightingManager to the database.
     */
    public static void initialize(DatabaseConnector dbConnector) {
        instance = new RatSightingManager(dbConnector);
        instance.isInit = true;
    }

    /**
     * This gets the currently initialized instance of RatSightingManager if no initialized instance
     * this returns null.
     * @return currently initialized instance of RatSightingManager
     */
    public static RatSightingManager getInstance() {
        if (instance == null || instance.isInit == false) {return null;}
        return instance;
    }

    /**
     * This method will get a block of rat sightings in order of most recent creation date.
     * The method will return null if there is an error.
     * @param size Size of the block of sighting reports to be returned
     * @param startRow How many results from the beginning of the set to start the block.
     * @return RatSighting array containing the block of sightings.
     */
    RatSighting[] getSightingBlock(int size, int startRow) throws SQLException {
        String statmentText = "SELECT * FROM sightingInfo" +
                " ORDER BY createdDate DESC" +
                " LIMIT ?";
            PreparedStatement statement = db.getStatement(statmentText);
            statement.setInt(1, size + startRow);
            ResultSet sightingInfo = db.query(statement);
            RatSighting[] results = new RatSighting[size];
            RatSighting test;
            lastRow = startRow;
            for (int i = 0; i < size; i++) {
                sightingInfo.absolute(lastRow);
                int key = sightingInfo.getInt("uKey");
                Date cDate = sightingInfo.getDate("createdDate");
                String aCode = sightingInfo.getString("agency");
                String complaintType = sightingInfo.getString("complaintType");

                String aLookup = "SELECT name FROM agencyLookup WHERE agency=?";
                PreparedStatement aLookupStmt = db.getStatement(aLookup);
                aLookupStmt.setString(1, aCode);
                ResultSet aLookupResults = db.query(aLookupStmt);
                aLookupResults.next();
                String aName = aLookupResults.getString("name");
                aLookupResults.close();

                String statusLookup = "SELECT * FROM sightingStatus WHERE uKey=?";
                PreparedStatement statusLookupStmt = db.getStatement(statusLookup);
                statusLookupStmt.setInt(1, key);
                ResultSet statusResults = db.query(statusLookupStmt);
                statusResults.next();
                String status = statusResults.getString("status");
                Date dueDate = statusResults.getDate("dueDate");
                Date closedDate = statusResults.getDate("closedDate");
                Date resUpdateDate = statusResults.getDate("resolutionActionUpdated");
                statusResults.close();

                String locationLookup = "SELECT * FROM sightingLocation WHERE uKey=?";
                PreparedStatement locLookup = db.getStatement(locationLookup);
                locLookup.setInt(1, key);
                ResultSet locResults = db.query(locLookup);
                locResults.next();
                String locType = locResults.getString("locationType");
                int zip = locResults.getInt("incidentZip");
                String city = locResults.getString("city");
                String borough = locResults.getString("borough");
                double lat = locResults.getDouble("latitude");
                double longitude = locResults.getDouble("longitude");
                locResults.close();
                test = new RatSighting(key, cDate, aCode, aName, complaintType, status,
                        dueDate, closedDate, resUpdateDate, locType, zip, city, borough, lat,
                        longitude);
                results[i] = test;
                lastRow++;
//                sightingInfo.next();
            }
            sightingInfo.close();
            Log.d(TAG, Integer.toString(lastRow));
            return results;
    }

    /**
     * This method gets a block of 100 rat sightings starting at the last sighting fetched.
     * @return RatSighting array of size 100 with the 100 sightings fetched.
     */
    RatSighting[] getNextBlock() throws SQLException {
        return getSightingBlock(100, lastRow);
    }

    /**
     * This method gets a block of rat sightings starting at the last sighting fetched.
     * @param size How many sightings to get.
     * @return RatSighting array with the sightings fetched.
     */
    RatSighting[] getNextBlock(int size) throws SQLException {
        return getSightingBlock(size, lastRow);
    }

    /**
     * Sets the RatSightingManager to grab the most recent entry (i.e. sets the row counter to 1).
     */
    void resetRowCounter() {
        lastRow = 1;
    }

    /**
     * Sets the row counter to the passed in value.
     * @param startRow Starting row to fetch sightings from.
     */
    void setRowCounter(int startRow) {
        lastRow = startRow;
    }
}
