package cs2340.gatech.edu.brodents;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class will provide unit testing for the method login in RatAppModel.
 * @author Rikesh Subedi
 * @version 1.0
 */
public class RikeshSubediLoginTest {
    private RatAppModel model;
    private DatabaseConnector connector;

    /* random info we will test with */
    private String user = "OptimusPrime";
    private String pass = "autobots";

    /**
     * Method for deleting user from DB, courtesy of Ben Templin
     * @throws SQLException if there was an SQL exception.
     */
    private void deleteUser() throws SQLException {
        String qText = "DELETE FROM users WHERE userName = ?";
        PreparedStatement stmt = connector.getStatement(qText);
        stmt.setString(1, "unitTestChecker");
        connector.update(stmt);
    }


    /**
     * Method to prepare database for testing.
     */
    @Before
    public void setUp() {
        RatAppModel.initialize();
        model = RatAppModel.getInstance();
        connector = model.getConnector();

        try {
            deleteUser();
        } catch (SQLException e) {
            Log.e("SQLException", "setup Exception");
        }

        String profileName = "UNIT TEST";
        String home = "Richmond, VA";
        boolean admin = false;
        model.registerUser(user,  pass, profileName, home, admin);
    }

    /**
     * Method to test login for an existing user with the wrong password.
     */
    @Test
    public void testExistingUserWrongPassword() {
        String wrongPass = "decepticons";
        assertFalse("Could login with incorrect password.", model.login(user, wrongPass));
    }

    /**
     * Method to test login for an existing user with the correct password.
     */
    @Test
    public void testExistingUserRightPassword() {
        assertTrue("Login with the correct password not successful.", model.login(user, pass));
    }

    /**
     * Method to test login for a nonexistent username.
     */
    @Test
    public void testNonexistentUser() {
        String nonUser = "Megatron";
        String wrongPass = "decepticons";
        assertFalse("Login with nonexistent user not rejected.", model.login(nonUser, wrongPass));
    }

    /**
     * Method to test login for a null username.
     */
    @Test
    public void testNullUser() {
        String wrongPass = "decepticons";
        assertFalse("Login with null user not rejected.", model.login(null, wrongPass));
    }

    /**
     * Method to test login for a null password.
     */
    @Test
    public void testNullPassword() {
        String nonUser = "Megatron";
        assertFalse("Login with null password not rejected.", model.login(nonUser, null));
    }

    /**
     * Method to test login for null username and password.
     */
    @Test
    public void testNullUserAndPass() {
        assertFalse("Login with null data not rejected.", model.login(null, null));
    }

    /**
     * Cleans app after testing.
     */
    @After
    public void clean() {
        model.setCurrentUser(null);

        try {
            deleteUser();
        } catch (SQLException e) {
            Log.e("SQLException", "cleanup Exception");
        }
    }
}
