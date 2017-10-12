package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

}
