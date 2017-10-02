package cs2340.gatech.edu.brodents;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Benjamin Yarmowich on 9/25/17.
 */
public class PasswordHasher {
/**
 * This class takes in a plain text password and returns the hashed version.
 * @param passwordToHash String to hash.
 * @param salt String to be prepended to password
 * @return Hashed String value.
 */
    public static String getSecurePassword(String salt, String passwordToHash){
        String generatedPassword = null;
        try {
            String passwordAndSalt = salt + passwordToHash;
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordAndSalt.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return generatedPassword;
    }
}
