package cs2340.gatech.edu.brodents;

import java.util.Date;
import java.util.List;

/**
 * Stores a selected rat
 * @author Benjamin Yarmowich
 * @version 1.0
 */

class RatSelected {
    private static RatSighting selected;

    /**
     * Selects and saves a rat from the list of Rats
     * @param position The location in the list of rats the selected rat is
     */
    public static void setRatSelected(int position){
        List<RatSighting> listOfRats = DataDisplayActivity.getRatData();
        selected = listOfRats.get(position);
    }

    /**
     * Saves a Rat Sighting
     * @param ratSighting the Rat Sighting to be saved
     */
    public static void setRatSelected(RatSighting ratSighting) {
        selected = ratSighting;
    }

    /**
     * Returns the selected Rat
     * @return the selected Rat
     */
    public static RatSighting getSelected(){
        return selected;
    }

    // Here go all of the getters!

    /**
     * Gets the Sighting's key
     * @return The sighting's unique key.
     */
    static int getKey() {return selected.getKey();}

    /**
     * @return The date the sighting was created.
     */
    static Date getCreatedDate() {return selected.getCreatedDate();}

    /**
     * @return The zip code for the sighting.
     */
    static int getZip() {return selected.getZip();}

    /**
     * @return The city of the sighting.
     */
    static CharSequence getCity() {return selected.getCity();}

    /**
     * @return The borough of the sighting.
     */
    static CharSequence getBorough() {return selected.getBorough();}

    /**
     * @return The latitude of the sighting.
     */
    static double getLatitude() {return selected.getLatitude();}

    /**
     * @return The longitude of the sighting.
     */
    static double getLongitude() {return selected.getLongitude();}

    /**
     * @return The address of the sighting location. Often null.
     */
    static CharSequence getAddress() {return selected.getAddress();}

    /**
     * @return The type of the sighting location. Often null.
     */
    static CharSequence getLocationType() {return selected.getLocationType();}

}
