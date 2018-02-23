package seniorproj.munchbox;

import android.media.Image;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Paul and Danny on 2/22/2018.
 */

public class JournalEntry
{
  private int identifier; //system-assigned
    public String nameOfDish;
    public String restaurantName;
    public int rating;
    public Image photo = null;
    public int frequency;
    private String description;
    public ArrayList<String> tags;
    public Date entryDate = null;

    /*Returns all information in the order of: name, restaurant, tags */
    public ArrayList<String> getKeywords() {
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.add(nameOfDish);
        returnList.add(restaurantName);
        for (String tag: tags) {
            returnList.add(tag);
        }
        return returnList;
    }

    public JournalEntry(Image photo) {
        rating = 0;
        nameOfDish = "";
        restaurantName = "";
        description = "";
        photo = photo;
        frequency = 1;
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
    }
}
