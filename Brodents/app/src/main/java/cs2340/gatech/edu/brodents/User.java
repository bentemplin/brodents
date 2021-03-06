package cs2340.gatech.edu.brodents;


import android.support.annotation.NonNull;

/**
 * This class represents a user for the Brodents app. This class is an information holder.
 * @author Ben Templin
 * @version 1.o
 */
class User {
    @NonNull
    private final String userName;
    private final String profileName;

    /**
     * Constructor for the User class
     * @param userName The User's username
     * @param profileName The User's profile/full name
     * @param address The User's address
     * @param isAdmin The User's admin status
     */
    public User(String userName, String profileName, String address, boolean isAdmin) {
        this.userName = userName;
        this.profileName = profileName;
    }

    /**
     * This method gets the User's username
     * @return The User's username
     */
    public String getUserName() {return userName;}

    /**
     * This method gets the User's full/profile name
     * @return The User's full/profile name
     */
    public String getProfileName() {return profileName;}

    /**
     * This method gets the User's address
     * @return The User's address
     */
    public String getAddress() {return userName;}

    /**
     * This method gets the User's admin status
     * @return The User's admin status
     */
    public String getAdmin() {return userName;}
}