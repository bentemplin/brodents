package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class will interface the Brodents app with the database of rat sightings. It is designed in
 * a singleton format.
 *
 * @author Ben Templin
 * @version 1.0
 */

final class RatSightingManager {
    private final DatabaseConnector db;
    private boolean isInit;
    private static RatSightingManager instance = null;
    private static final String TAG = "RatSightingManager";

    private RatSightingManager(DatabaseConnector connector) {
        db = connector;
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
        if ((instance == null) || !instance.isInit) {return null;}
        return instance;
    }

    /**
     * This method gets a block of 100 rat sightings starting at the last sighting fetched.
     * @param lastRow Starting row from which to fetch the sightings.
     * @return RatSighting array of size 100 with the 100 sightings fetched.
     */
    RatSighting[] getNextBlock(int lastRow) throws SQLException {
        return getNextBlock(100, lastRow);
    }

    /*
        getNextBlock is super long because there is a lot of information which must be pulled from
        the database about each rat sighting.
     */

    /**
     * This method gets a block of rat sightings starting at the last sighting fetched.
     * @param size How many sightings to get.
     * @param last Starting row from which to fetch the sightings.
     * @return RatSighting array with the sightings fetched.
     */
    RatSighting[] getNextBlock(int size, int last) throws SQLException {
        int lastRow = last;
        String statementText = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL" +
                " JOIN sightingLocation NATURAL JOIN agencyLookup" +
                " ORDER BY createdDate DESC" +
                " LIMIT ?";
        PreparedStatement statement = db.getStatement(statementText);
        statement.setInt(1, size + lastRow);
        ResultSet sightingInfo = db.query(statement);
        RatSighting[] results = new RatSighting[size];
        RatSighting test;
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
            if ((createdBy != null) && !createdBy.isEmpty()) {
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
        return results;
    }

    /*
        insertSighting is super long because there is a lot of information which must be input into
        the database about each rat sighting.
     */

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
     * @return Boolean indicating the success of the insertion
     */
    boolean insertSighting(String complaintType, String locationType, int zip,
                           String city, String borough, String address, double latitude,
                           double longitude) {
        Timestamp currentDate = new Timestamp(new Date().getTime());
        String agencyCode = "BRO";
        try {
            ResultSet maxKeySet = db.query(db.getStatement(
                    "SELECT MAX(uKey) FROM sightingInfo"));
            maxKeySet.next();
            int key = maxKeySet.getInt("MAX(uKey)") + 1;
            maxKeySet.close();

            String infoText = "INSERT INTO sightingInfo(uKey, createdDate, agency, complaintType, "
                    + "createdBy) VALUES (?,?,?,?,?)";
            PreparedStatement infoStmt = db.getStatement(infoText);
            infoStmt.setInt(1, key);
            infoStmt.setTimestamp(2, currentDate);
            infoStmt.setString(3, agencyCode);
            infoStmt.setString(4, complaintType);
            RatAppModel model = RatAppModel.getInstance();
            if (model.getCurrentUser().getUserName() != null) {
                infoStmt.setString(5, model.getCurrentUser().getUserName());
            } else {
                infoStmt.setNull(5, Types.VARCHAR);
            }
            db.update(infoStmt);
            infoStmt.close();

            String statusText = "INSERT INTO sightingStatus(uKey, status, dueDate, closedDate"
                    + ", resolutionActionUpdated) VALUES (?,?,?, null, ?)";
            PreparedStatement statusStmt = db.getStatement(statusText);
            statusStmt.setInt(1, key);
            statusStmt.setString(2, "Pending");
            statusStmt.setNull(3, Types.TIMESTAMP);
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
            if ((city != null) && !borough.isEmpty()) {
                locStmt.setString(4, city);
            } else {
                locStmt.setNull(4, Types.VARCHAR);
            }
            if ((borough != null) && !borough.isEmpty()) {
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
            if ((address != null) && !address.isEmpty()) {
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
    List<RatSighting> getNewSightings(Date start) {
        try {
            String query = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL JOIN" +
                    " sightingLocation NATURAL JOIN agencyLookup WHERE createdDate >= ? ORDER BY" +
                    " createdDate ASC";
            PreparedStatement stmt = db.getStatement(query);
            Timestamp sqlDate = new Timestamp(start.getTime());
            stmt.setTimestamp(1, sqlDate);
            ResultSet infoSet = db.query(stmt);
            return resultSetToRatList(infoSet);

        } catch (SQLException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets sightings from the database between the start and end dates
     * @param start Oldest sightings to fetch
     * @param end Newest sightings to fecth
     * @return ArrayList containing the fetched RatSightings
     * @throws SQLException Throws an SQL exception if an invalid query is passed in. Since there
     *                      is no user input for this step, it should never be thrown.
     */
    ArrayList<RatSighting> getSightingsBetween(Date start, Date end) throws SQLException {
        Timestamp startTime = new Timestamp(start.getTime());
        Timestamp endTime = new Timestamp(end.getTime());
        String qText = "SELECT * FROM sightingInfo NATURAL JOIN sightingStatus NATURAL JOIN" +
                " sightingLocation NATURAL JOIN agencyLookup WHERE createdDate >= ? AND createdDate"
                + " <= ? ORDER BY createdDate ASC";
        PreparedStatement statement = db.getStatement(qText);
        statement.setTimestamp(1, startTime);
        statement.setTimestamp(2, endTime);
        ResultSet results = db.query(statement);
        return resultSetToRatList(results);
    }

    private ArrayList<RatSighting> resultSetToRatList (ResultSet infoSet) throws SQLException {
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
        if (sightings.isEmpty()) {return null;}
        return sightings;
    }
}
