package cs2340.gatech.edu.brodents;

import android.util.Log;

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

public class RatAppModel {
    private DatabaseConnector db;
    private boolean dbInitialized;
    private static RatAppModel model;

    private RatAppModel(String userName, String password, String host) {
        try {
            db = new DatabaseConnector(userName, password, host);
            dbInitialized = true;
        } catch (SQLException e) {
            Log.e("RatAppModel", e.getMessage());
            dbInitialized = false;
        }
    }

    /**
     * This method initializes the model and tries to create a database connection.
     */
    public static void initialize() {
        model = new RatAppModel("ratapp", "2Z2MqYE!cLgNJu8R",
                "104.236.213.171:3306/rats");
        Log.d("RatAppModel", "Initialized");
    }

    /**
     * This method gets the current RatAppModel.
     * @return RatAppModel currently being maintained.
     */
    public static RatAppModel getInstance() {return model;}

    /**
     * This method returns whether the Database connection has been initialized.
     * @return Boolean whether the Database connection has been initialized
     */
    public boolean isDbInitialized() {return dbInitialized;}

    /**
     * This method tests whether the passed in username and password are valid credentials
     * @param userName The username to test
     * @param password The password (in plaintext) to test
     * @return boolean of the result of the test
     */
    public boolean testCredentials(String userName, String password) {
        String getUsersText = "SELECT userName, password, salt FROM users WHERE username=?";
        ResultSet results;
        try {
            PreparedStatement statement = db.getStatement(getUsersText);
            statement.setString(1, userName);
            results = db.query(statement);
            if (!results.next()) {
                // No entries in DB for passed in username
                results.close();
                return false;
            }
            String dbPass = results.getString("password");
            int salt = results.getInt("salt");
            String hashPass = PasswordHasher.getSecurePassword(Integer.toString(salt),
                    password);
            results.close();
            if (dbPass.equals(hashPass)) {
                Log.i("testCredentials", "auth success");
                return true;
            } else {
                Log.i("testCredentials", "auth failed");
                return false;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }
}
