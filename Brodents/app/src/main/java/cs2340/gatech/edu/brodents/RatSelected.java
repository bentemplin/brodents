package cs2340.gatech.edu.brodents;

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
}
