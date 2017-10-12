package cs2340.gatech.edu.brodents;

import java.util.List;

/**
 * Created by Benjamin Yarmowich on 10/10/2017.
 */

public class RatSelected {
    private List<RatSighting> listOfRats;
    private static RatSighting selected;

    /**
     * Selects a rat from the list of Rats
     * @param position The location in the list of rats the selected rat is
     */
    public RatSelected(int position){
        listOfRats = DataDisplayActivity.getRatData();
        selected = listOfRats.get(position);
    }

    /**
     * Returns the selected Rat
     * @return the selected Rat
     */
    public static RatSighting getSelected(){
        return selected;
    }
}
