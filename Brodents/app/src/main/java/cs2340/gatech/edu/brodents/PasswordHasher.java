package cs2340.gatech.edu.brodents;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Benjamin Yarmowich on 9/25/17.
 */
class PasswordHasher {
    private static final int BYTE_MASK = 0xff;
    private static final int BYTE_OFFSET = 0x100;
    private static final int RADIX = 16;
/**
 * This class takes in a plain text password and returns the hashed version.
 * @param passwordToHash String to hash.
 * @param salt String to be prepended to password
 * @return Hashed String value.
 */
    public String getSecurePassword(String salt, String passwordToHash){
        String generatedPassword = null;
        try {
            String passwordAndSalt = salt + passwordToHash;
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordAndSalt.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & BYTE_MASK) + BYTE_OFFSET, RADIX).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }
}
