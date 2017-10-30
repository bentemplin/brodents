package cs2340.gatech.edu.brodents;

import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/10/2017.
 */

class RatSelected {
    private static RatSighting selected;

    /**
     * Selects and saves a rat from the list of Rats
     * @param position The location in the list of rats the selected rat is
     */
    public RatSelected(int position){
        List<RatSighting> listOfRats = DataDisplayActivity.getRatData();
        selected = listOfRats.get(position);
    }

    /**
     * Saves a Rat Sighting
     * @param ratSighting the Rat Sighting to be saved
     */
    public RatSelected(RatSighting ratSighting) {
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
