package cs2340.gatech.edu.brodents;

/**
 * This interface provides click to the text views
 * @author Benjamin Yarmowich
 * @version 1.o
 */

@SuppressWarnings("EmptyMethod")
interface ClickListener {
    void onPositionClicked(int position);

    void onLongClicked(int position);
}