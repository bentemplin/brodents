package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;

/**
 * This class will interface the Brodents app with the database of rat sightings. It is designed in
 * a singleton format.
 *
 * @author Ben Templin
 * @version 1.0
 */

class RatSightingManager {
    private DatabaseConnector db;
    private boolean isInit;
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
    static void initialize(DatabaseConnector dbConnector) {
        instance = new RatSightingManager(dbConnector);
        instance.isInit = true;
    }

    /**
     * This gets the currently initialized instance of RatSightingManager if no initialized instance
     * this returns null.
     * @return currently initialized instance of RatSightingManager
     */
    static RatSightingManager getInstance() {
        if (instance == null || !instance.isInit) {return null;}
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
        String statementText = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL" +
                " JOIN sightingLocation NATURAL JOIN agencyLookup" +
                " ORDER BY createdDate DESC" +
                " LIMIT ?";
            PreparedStatement statement = db.getStatement(statementText);
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
                String createdBy = sightingInfo.getString("createdBy");
                String aName = sightingInfo.getString("name");
                String status = sightingInfo.getString("status");
                Date dueDate = sightingInfo.getDate("dueDate");
                Date closedDate = sightingInfo.getDate("closedDate");
                Date resUpdateDate = sightingInfo.getDate("resolutionActionUpdated");
                String locType = sightingInfo.getString("locationType");
                int zip = sightingInfo.getInt("incidentZip");
                String city = sightingInfo.getString("city");
                String borough = sightingInfo.getString("borough");
                double lat = sightingInfo.getDouble("latitude");
                double longitude = sightingInfo.getDouble("longitude");
                String address = sightingInfo.getString("address");
                if (createdBy != null && createdBy.length() > 0) {
                    test = new RatSighting(key, cDate, aCode, aName, complaintType, status,
                            dueDate, closedDate, resUpdateDate, locType, zip, city, borough, address,
                            lat, longitude);
                } else {
                    test = new RatSighting(key, cDate, aCode, aName, complaintType, status,
                            dueDate, closedDate, resUpdateDate, locType, zip, city, borough, address,
                            lat, longitude, createdBy);
                }
                results[i] = test;
                lastRow++;
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
     * Lets you insert a sighting into the database. Make things null if they aren't specified.
     * If zip, latitude, or longitude aren't specified, pass in 0.
     * @param complaintType Complaint type of the sighting
     * @param locationType Type of location
     * @param zip Incident zip code, set 0 if unknown
     * @param city City where sighting occurred
     * @param borough Borough where sighting occurred
     * @param address Address where sighting occurred
     * @param latitude Latitude of sighting, set 0 if unknown
     * @param longitude Longitude of sighting, set 0 if unknown
     * @param dueDate Due date
     * @return Boolean indicating the success of the insertion
     */
    boolean insertSighting(String complaintType, String locationType, int zip,
                           String city, String borough, String address, double latitude, double longitude,
                           Date dueDate) {
        Timestamp currentDate = new Timestamp(new Date().getTime());
        String agencyCode = "BRO";
        try {
            ResultSet maxKeySet = db.query(db.getStatement("SELECT MAX(uKey) FROM sightingInfo"));
            maxKeySet.next();
            int key = maxKeySet.getInt("MAX(uKey)") + 1;
            maxKeySet.close();

            String infoText = "INSERT INTO sightingInfo(uKey, createdDate, agency, complaintType, " +
                    "createdBy) VALUES (?,?,?,?,?)";
            PreparedStatement infoStmt = db.getStatement(infoText);
            infoStmt.setInt(1, key);
            infoStmt.setTimestamp(2, currentDate);
            infoStmt.setString(3, agencyCode);
            infoStmt.setString(4, complaintType);
            infoStmt.setString(5, RatAppModel.getInstance().getCurrentUser().getUserName());
            db.update(infoStmt);
            infoStmt.close();

            String statusText = "INSERT INTO sightingStatus(uKey, status, dueDate, closedDate"
                    + ", resolutionActionUpdated) VALUES (?,?,?, null, ?)";
            PreparedStatement statusStmt = db.getStatement(statusText);
            statusStmt.setInt(1, key);
            statusStmt.setString(2, "Pending");
            if (dueDate != null) {
                statusStmt.setTimestamp(3, new Timestamp(dueDate.getTime()));
            } else {
                statusStmt.setNull(3, Types.TIMESTAMP);
            }
            statusStmt.setTimestamp(4, currentDate);
            db.update(statusStmt);
            statusStmt.close();

            String locationText = "INSERT INTO sightingLocation(uKey, locationType, incidentZip," +
                    " city, borough, latitude, longitude, address) VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement locStmt = db.getStatement(locationText);
            locStmt.setInt(1, key);
            locStmt.setString(2, locationType);
            if (zip != 0) {
                locStmt.setInt(3, zip);
            } else {
                locStmt.setNull(3, Types.INTEGER);
            }
            if (city != null && borough.length() > 0) {
                locStmt.setString(4, city);
            } else {
                locStmt.setNull(4, Types.VARCHAR);
            }
            if (borough != null && borough.length() >0) {
                locStmt.setString(5, borough);
            } else {
                locStmt.setNull(5, Types.VARCHAR);
            }
            if (latitude != 0) {
                locStmt.setDouble(6, latitude);
            } else {
                locStmt.setNull(6, Types.DECIMAL);
            }
            if (longitude != 0) {
                locStmt.setDouble(7, longitude);
            } else {
                locStmt.setNull(7, Types.DECIMAL);
            }
            if (address != null && address.length() > 0) {
                locStmt.setString(8, address);
            } else {
                locStmt.setNull(8, Types.VARCHAR);
            }
            db.update(locStmt);
            locStmt.close();
            return true;
        } catch (SQLException e) {
            Log.e("Insert Sighting", e.getMessage());
            return false;
        }
    }

    /**
     * Gets a rat report based on the key.
     * @param key Key for the rat report to fetch.
     * @return The RatSighting with the passed in key. Null if no sighting is found for that key.
     */
    RatSighting getSighting(int key) {
        String infoTxt = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL JOIN" +
                " sightingLocation NATURAL JOIN agencyLookup WHERE uKey=?";
        try {
            PreparedStatement infoStmt = db.getStatement(infoTxt);

            infoStmt.setInt(1, key);

            ResultSet infoSet = db.query(infoStmt);
            if (!infoSet.next()) {return null;}
            String agencyCode = infoSet.getString("agency");
            Date cDate = infoSet.getDate("createdDate");
            String complaintType = infoSet.getString("complaintType");
            String createdBy = infoSet.getString("createdBy");
            String agencyName = infoSet.getString("name");
            String status = infoSet.getString("status");
            Date dueDate = infoSet.getDate("dueDate");
            Date closedDate = infoSet.getDate("closedDate");
            Date resActionUpdated = infoSet.getDate("resolutionActionUpdated");
            String locType = infoSet.getString("locationType");
            int incidentZip = infoSet.getInt("incidentZip");
            String city = infoSet.getString("city");
            String borough = infoSet.getString("borough");
            String address = infoSet.getString("address");
            double latitude = infoSet.getDouble("latitude");
            double longitude = infoSet.getDouble("longitude");
            infoSet.close();

            return new RatSighting(key, cDate, agencyCode, agencyName, complaintType, status,
                    dueDate, closedDate, resActionUpdated, locType, incidentZip, city, borough,
                    address, latitude, longitude, createdBy);
        } catch (SQLException e) {
            Log.e("GetSighting", e.getMessage());
            return null;
        }
    }

    /**
     * This will get all of the ratSightings created since the start date passed in.
     * @param start Date after which to get sightings
     * @return New RatSightings
     */
    ArrayList<RatSighting> getNewSightings(Date start) {
        try {
            String query = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL JOIN" +
                    " sightingLocation NATURAL JOIN agencyLookup WHERE createdDate >= ? ORDER BY" +
                    " createdDate ASC";
            PreparedStatement stmt = db.getStatement(query);
            Timestamp sqlDate = new Timestamp(start.getTime());
            stmt.setTimestamp(1, sqlDate);
            ResultSet infoSet = db.query(stmt);
            ArrayList<RatSighting> sightings = new ArrayList<>();
            while (infoSet.next()) {
                int key = infoSet.getInt("uKey");
                String agencyCode = infoSet.getString("agency");
                Date cDate = infoSet.getDate("createdDate");
                String complaintType = infoSet.getString("complaintType");
                String createdBy = infoSet.getString("createdBy");
                String agencyName = infoSet.getString("name");
                String status = infoSet.getString("status");
                Date dueDate = infoSet.getDate("dueDate");
                Date closedDate = infoSet.getDate("closedDate");
                Date resActionUpdated = infoSet.getDate("resolutionActionUpdated");
                String locType = infoSet.getString("locationType");
                int incidentZip = infoSet.getInt("incidentZip");
                String city = infoSet.getString("city");
                String borough = infoSet.getString("borough");
                String address = infoSet.getString("address");
                double latitude = infoSet.getDouble("latitude");
                double longitude = infoSet.getDouble("longitude");

                sightings.add(new RatSighting(key, cDate, agencyCode, agencyName, complaintType, status,
                        dueDate, closedDate, resActionUpdated, locType, incidentZip, city, borough,
                        address, latitude, longitude, createdBy));

            }
            infoSet.close();
            return sightings;

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
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
