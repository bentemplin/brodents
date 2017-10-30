package cs2340.gatech.edu.brodents;

import android.util.Log;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

/**
 * A class allowing the interface of a Java application and an SQL database.
 * Implemented in such a way as to create the DatabaseConnector as a singleton
 * object.
 *
 * @version 1.0
 * @author Ben Templin
 */
class DatabaseConnector {
    private String dbUserName;
    private String dbPassword;
    private String host;
    private Connection dbConnection;
    private static boolean debug;

    /* !!!!!!!!!!
     * Here are all of the methods that will actually enable the class to talk
     * to the database set in the instance of DatabaseConnector. This is
     * class is implemented so that there is only one instance of the connector
     * at a time. These are all private methods to restrict what users can do
     * to manipulate the database and the connector itself.
     */
    DatabaseConnector(String userName, String password,
                      String hostName) throws SQLException {
        try {
            dbUserName = userName;
            dbPassword = password;
            this.host = "jdbc:mysql://" + hostName;
            debug = true;
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection finalConnection =
                DriverManager.getConnection(this.host, dbUserName, dbPassword);
            debugMessage("SQL Connection Succeeded");
            dbConnection = finalConnection;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            debugMessage(Arrays.toString(e.getStackTrace()));
        }
    }

    //Logs param text only if debug param is set to false
    private void debugMessage(String message) {
        if (debug) {
            Log.d("DatabaseConnector", message);
        }
    }

    /**
     * This method allows you to query a table in the database.
     * @param statement A PreparedStatement of the query to be executed.
     * @return A ResultSet of the results of the query.
     * @throws SQLException
     */
    public ResultSet query(PreparedStatement statement) throws SQLException{
        return statement.executeQuery();
    }

    /**
     * This method allows you to update a table in the database.
     * @param statement A PreparedStatement of the update to be executed.
     * @throws SQLException
     */
    public void update(PreparedStatement statement) throws SQLException {
        statement.executeUpdate();
    }

    /**
     * This method will sets whether the DatabaseConnector is in debug mode and
     * will generate debugging outputs.
     *
     * @param value A boolean. True for debug mode on, false for off.
     */
    public static void setDebug(boolean value) {
        debug = value;
    }

    /**
     * This method gets whether the DatabaseConnector is in debug mode.
     * @return Wheter the DatabaseConnector is in debug mode as a boolean.
     */
    public static boolean getDebug() {
        return debug;
    }

    /**
     * This method gets the hostname of the currently connected database.
     * @return The hostname as a String.
     */
    public String getHost() {
        return host;
    }

    /**
     * This method gets a PreparedStatement that can be used to query the
     * database.
     * @param text Text for the prepared statement.
     * @return A PreparedStatement object ready to be parameterized
     * @throws SQLException
     */
    public PreparedStatement getStatement(String text) throws SQLException {
        return dbConnection.prepareStatement(text);
    }

    /**
     * This method changes the what the database the DatabaseConnector is
     * connected to.
     *
     * @param userName The username as a String for the database.
     * @param pass The password as a String for the database.
     * @param host The hostname of the database as a String.
     * @return
     */
    public boolean changeConnection(String userName, String pass,
        String host) {
        boolean changed;
        Connection tempConnection = dbConnection;
        try {
            dbConnection.close();
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String tempHost = "jdbc:mysql://" + host;
            Connection finalConnection = DriverManager.getConnection(tempHost,
                userName, pass);
            dbUserName = userName;
            dbPassword = pass;
            this.host = tempHost;
            debugMessage("Connection Changed");
            changed = true;
            dbConnection = finalConnection;
        } catch (SQLException e) {
            changed = false;
            dbConnection = tempConnection;
        } catch (Exception e) {
            debugMessage(Arrays.toString(e.getStackTrace()));
            changed = false;
        }
        return changed;
    }
}