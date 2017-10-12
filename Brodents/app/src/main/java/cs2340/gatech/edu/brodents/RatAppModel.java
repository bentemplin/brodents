package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.security.KeyRep;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * This class will handle all of the business logic for our app. Basically, it'll have methods to
 * run all of the backend, especially stuff having to do with connecting to the database.
 *
 * @author Ben Templin
 * @version 1.0
 */

class RatAppModel {
    private DatabaseConnector db;
    private boolean dbInitialized;
    private User currentUser;
    private RatSightingManager sightingManager;
    private static RatAppModel model;

    private RatAppModel(String userName, String password, String host) {
        try {
            db = new DatabaseConnector(userName, password, host);
            dbInitialized = true;
            currentUser = null;
            RatSightingManager.initialize(db);
            sightingManager = RatSightingManager.getInstance();
        } catch (SQLException e) {
            Log.e("RatAppModel", e.getMessage());
            dbInitialized = false;
        }
    }

    static void checkInitialization() {
        if (model == null || !model.dbInitialized) RatAppModel.initialize();
    }

    /**
     * This method initializes the model and tries to create a database connection.
     */
    static void initialize() {
        model = new RatAppModel("ratapp", "2Z2MqYE!cLgNJu8R",
                "104.236.213.171:3306/rats");
        Log.d("RatAppModel", "Initialized");
    }

    /**
     * This method gets the current RatAppModel.
     * @return RatAppModel currently being maintained.
     */
    static RatAppModel getInstance() {return model;}

    /**
     * This method returns whether the Database connection has been initialized.
     * @return Boolean whether the Database connection has been initialized
     */
    boolean isDbInitialized() {return dbInitialized;}

    /**
     * This method gets the DatabaseConnector for the model.
     * @return The DatabaseConnector
     */
    DatabaseConnector getConnector() {return db;}

    /**
     * This method gets the model's sighting manager.
     * @return The model's sighting manager.
     */
    RatSightingManager getSightingManager() {return sightingManager;}
    /**
     * This method gets the current user's username.
     * @return String of current user's username
     */
    User getCurrentUser() {return currentUser;}

    /**
     * This method clears the current user
     */
    void clearCurrentUser() {
        currentUser = null;
    }

    /**
     * Sets the current user to the one passed in.
     * @param newCurrentUser The new current user
     */
    void setCurrentUser(User newCurrentUser) {
        currentUser = newCurrentUser;
    }

    /**
     * This method logs the user in with the passed in credentials if correct. It also sets the
     * current user if the login is successful.
     * @param userName The username to login with
     * @param password The password (in plaintext) to login with
     * @return boolean of the result of the login
     */
    boolean login(String userName, String password) {
        RatAppModel.checkInitialization();
        String getUsersText = "SELECT * FROM users WHERE username=?";
        ResultSet results;
        boolean loginStatus;
        try {
            PreparedStatement statement = db.getStatement(getUsersText);
            statement.setString(1, userName);
            results = db.query(statement);
            if (!results.next()) {
                // No entries in DB for passed in username
                results.close();
                loginStatus = false;
            } else {
                String dbPass = results.getString("password");
                int salt = results.getInt("salt");
                String hashPass = PasswordHasher.getSecurePassword(Integer.toString(salt),
                        password);
                if (dbPass.equals(hashPass)) {
                    Log.i("login", "auth success");
                    loginStatus = true;
                    String profileName = results.getString("profileName");
                    String address = results.getString("homeLocation");
                    boolean isAdmin = results.getBoolean("isAdmin");
                    currentUser = new User(userName, profileName, address, isAdmin);
                } else {
                    Log.i("login", "auth failed");
                    loginStatus = false;
                }
                results.close();
            }
            return loginStatus;
        } catch (SQLException e) {
            Log.e("login", e.getMessage());
            return false;
        }
    }

    /**
     * This method registers a user in the database. After registration they will instantly be able
     * to log in to the system.
     *
     * NOTE: this method does not do any input validation. You should check
     * to make sure usernames, passwords, profile names, and home locations match your specific
     * criteria before calling this method.
     * @param userName The username under which to register the user
     * @param password The password to use for the user's account
     * @param profileName The profile name to display for the user
     * @param homeLocation The user's home location
     * @param isAdmin A boolean for whether to make the user an administrator.
     * @return An integer code indicating the success of the registration. 0 if registration
     *         succeeds, 1 if the registration fails because the username is already taken, and
     *         2 if a SQLException occurs during registration;
     */
    int registerUser(String userName, String password, String profileName,
        String homeLocation, boolean isAdmin) {
        RatAppModel.checkInitialization();
        SecureRandom saltShaker = new SecureRandom();
        try {
            PreparedStatement checkStatement = db.getStatement("SELECT * FROM users WHERE userName"
            + "=?");
            checkStatement.setString(1, userName);
            ResultSet checkResults = db.query(checkStatement);
            if (checkResults.next()) { //Check for username already in use
                return 1;
            } else {
                int salt = saltShaker.nextInt(32);
                String hashedPass = PasswordHasher.getSecurePassword(Integer.toString(salt),
                        password);
                String registrationText = "INSERT INTO users(userName, password, profileName, "
                        + "homeLocation, salt, isAdmin) VALUES(?, ?, ?, ?, ?, ?)";
                PreparedStatement registerStatement = db.getStatement(registrationText);
                registerStatement.setString(1, userName);
                registerStatement.setString(2, hashedPass);
                registerStatement.setString(3, profileName);
                registerStatement.setString(4, homeLocation);
                registerStatement.setInt(5, salt);
                registerStatement.setBoolean(6, isAdmin);
                db.update(registerStatement);
                Log.d("Register User", "Success for userName = " + userName);
                return 0;
            }
        } catch (SQLException e) {
            Log.e("Register User", e.getMessage());
            return 2;

        }
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
            int key = maxKeySet.getInt("MAX(uKey)") + 1;
            maxKeySet.close();

            String infoText = "INSERT INTO sightingInfo(uKey, createdDate, agency, complaintType, " +
                    "createdBy) VALUES (?,?,?,?,?)";
            PreparedStatement infoStmt = db.getStatement(infoText);
            infoStmt.setInt(1, key);
            infoStmt.setTimestamp(2, currentDate);
            infoStmt.setString(3, agencyCode);
            infoStmt.setString(4, complaintType);
            infoStmt.setString(5, currentUser.getUserName());
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
}
