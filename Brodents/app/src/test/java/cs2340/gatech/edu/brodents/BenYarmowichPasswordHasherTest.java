package cs2340.gatech.edu.brodents;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * This class will provide unit testing for the method
 * getSecurePassword(String salt, String passwordToHash) in PasswordHasher.
 * @author Benjamin Yarmowich
 * @version 1.o
 */

public class BenYarmowichPasswordHasherTest {
    private PasswordHasher hasher;

    /**
     * This method will prepare the database to run the tests
     */
    @Before
    public void setUp() {
        hasher = new PasswordHasher();
    }

    /**
     * Tests hashing the password "password" with salt "1234"
     */
    @Test
    public void testSimplePassword() {
        String pass = "password";
        String salt = "1234";
        String hashedPassword = hasher.getSecurePassword(salt, pass);
        assertEquals(hashedPassword, "0b6571d043ac6b01fa45da96068045e07ff695b10b9c6157dab" +
                "41a3392b65779a19662cc6e43f8abe528a4c933488c24df9a0940784b94ae22cd9b8cc1a75647");
    }

    //Tests that hashing a password twice returns the same string
    @Test
    public void testPasswordRepeat() {
        String pass = "password";
        String salt = "1234";
        String hashedPassword1 = hasher.getSecurePassword(salt, pass);
        String hashedPassword2 = hasher.getSecurePassword(salt, pass);
        assertEquals(hashedPassword1, hashedPassword2);
    }

    //tests hashing the password with salt = null
    @Test
    public void testNullSalt() {
        String pass = "password";
        String salt = null;
        String hashedPassword = hasher.getSecurePassword(salt, pass);
        assertEquals(hashedPassword, "d5957a3fa4d2268453433c828516bc0092e092080e5d67705f" +
                "8b34a8ff07dd42751d9845a749871e313420966b3c6d8a1611b7a770ddc0796eb73a32ac882dbd");
    }

    //tests hashing null password and null salt
    @Test
    public void testNullPasswordNullSalt() {
        String pass = null;
        String salt = null;
        String hashedPassword = hasher.getSecurePassword(salt, pass);
        assertEquals(hashedPassword, "89f3e6ee557cab8e6cc36e573e7037a1aa0694e0e7cb90965dc7" +
                "267848167e2eff7f4d80a6836e51af863e919355f6736637b0fe45f2dbb8a04964f20497381c");
    }

    //tests hashing a null password but a constant salt
    @Test
    public void testNullPassword() {
        String pass = null;
        String salt = "1234";
        String hashedPassword = hasher.getSecurePassword(salt, pass);
        assertEquals(hashedPassword, "f09f7375f36f1e689b9223285160f792bb73375a9dfdf6ace185" +
                "6678c2b8102b41f2be501c2e7b2778db8c70bec8f5d33e994847558cb556a527bad7dda64c12");
    }

    //tests hashing a password with a different salt yields a different string
    @Test
    public void testDifferentSalts() {
        String pass = "password";
        String salt1 = "1234";
        String salt2 = "salt";
        String hashedPassword1 = hasher.getSecurePassword(salt1, pass);
        String hashedPassword2 = hasher.getSecurePassword(salt2, pass);
        assertNotEquals(hashedPassword1, hashedPassword2);
    }

    //tests hashing 2 passwords with a same salt yields a different string
    @Test
    public void testDifferentPassword() {
        String pass1 = "password";
        String pass2 = "123456789";
        String salt = "salt";
        String hashedPassword1 = hasher.getSecurePassword(salt, pass1);
        String hashedPassword2 = hasher.getSecurePassword(salt, pass2);
        assertNotEquals(hashedPassword1, hashedPassword2);
    }
}