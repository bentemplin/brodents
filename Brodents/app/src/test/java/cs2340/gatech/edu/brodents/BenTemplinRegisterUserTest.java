package cs2340.gatech.edu.brodents;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class will provide unit testing for the method registerUser in RatAppModel.
 * @author bentempin
 * @version 1.o
 */
public class BenTemplinRegisterUserTest {
    private RatAppModel model;
    private DatabaseConnector connector;

    private void deleteUser() throws SQLException {
        String qText = "DELETE FROM users WHERE userName = ?";
        PreparedStatement stmt = connector.getStatement(qText);
        stmt.setString(1, "unitTestChecker");
        connector.update(stmt);
    }

    /**
     * This method will prepare the database to run the tests
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
    }

    /**
     * Tests adding a user not in the database
     */
    @Test
    public void testNonExistentUser() {
        String userName = "unitTestChecker";
        String pass = "password";
        String profileName = "UNIT TEST";
        String homeLocation = "Atlanta, GA";
        boolean isAdmin = false;
        assertEquals("Could not add new user",
                0, model.registerUser(userName, pass, profileName, homeLocation, isAdmin));
    }

    /**
     * This method confirms that a newly added user can be logged in
     */
    @Test
    public void testLoginOfNewUser() {
        String userName = "unitTestChecker";
        String pass = "password";
        String profileName = "UNIT TEST";
        String homeLocation = "Atlanta, GA";
        boolean isAdmin = false;
        model.registerUser(userName, pass, profileName, homeLocation, isAdmin);
        assertTrue("Could not log in newly added user", model.login(userName, pass));
    }

    /**
     * Tests trying to add a user that already exists in the database
     */
    @Test
    public void testExistingUser() {
        String userName = "user";
        String pass = "blah";
        String profileName = "blah blah";
        String homeLocation = "blah blah";
        boolean isAdmin = false;
        assertEquals("Attempt to add existing user was not rejected",
                1, model.registerUser(userName, pass, profileName,
                homeLocation, isAdmin));
    }



    /**
     * This method restores the db and model to their original state
     */
    @After
    public void cleanupDb() {
        model.setCurrentUser(null);
        try {
            deleteUser();
        } catch (SQLException e) {
            Log.e("SQLException", "cleanup Exception");
        }
    }

}
