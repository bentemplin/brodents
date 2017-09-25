package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.sql.SQLException;

/**
 * This class will connect our Android app to the database hosting all
 * of the information for our CS2340 project. Basically a skin that sets up
 * the database connector with the correct connection to a database.
 *
 * @author Ben Templin
 * @version 1.0
 */
public class RatAppConnector extends DatabaseConnector {
    private static boolean initialized = false;

    private static DatabaseConnector connector;

    /**
     * This method initializes the connection to the database.
     */
    public static void initialize() throws SQLException {
        connector = new DatabaseConnector("ratapp", "2Z2MqYE!cLgNJu8R",
            "104.236.213.171:3306/rats");
        Log.d("RatAppConnector", "Initialized");
        initialized = true;
    }

    public static DatabaseConnector getInstance() {
        return connector;
    }

    public static boolean isInitialized() {return initialized;}
}