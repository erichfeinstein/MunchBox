package seniorproj.munchbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Paul and Danny on 2/22/2018.
 */

public class JournalEntry implements Comparable<JournalEntry>
{
  private int identifier; //system-assigned
    private String nameOfDish;
    private String restaurantName;
    private int rating;
    public File photo = null;
    private int frequency;
    private String description;
    private ArrayList<String> tags = new ArrayList<String>();
    private Date entryDate = null;
    private Bitmap thumbnail = null;
    private String photoPath;

    public JournalEntry(Bitmap thumb, String path)
    {
        rating = 0;
        nameOfDish = "";
        restaurantName = "";
        //restaurantName = GoogleFindRestaurantName();
        //Generate tags
        PhotoAnalyzer labelGen = new PhotoAnalyzer(path);
   //     tags = (ArrayList)labelGen.getLabels();
        description = "";
        thumbnail = thumb;
        photoPath = path;
        //this.photoID = R.drawable.sample_image;
        frequency = 1;
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
    }

    public JournalEntry() {
        rating = 0;
        nameOfDish = "";
        restaurantName = "";
        //restaurantName = GoogleFindRestaurantName();
//        PhotoAnalyzer labelGen = new PhotoAnalyzer(photoPath);
//        tags = (ArrayList)labelGen.getLabels();
        description = "";
        frequency = 1;
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
        if (tags != null && tags.size() > 0) {
            nameOfDish = tags.get(0);
        }
    }

    //Used for sorting in MainActivity
    //Note: Only for comparing date of entry creation. Also the default sort option
    @Override
    public int compareTo(JournalEntry otherEntry) {
        return getEntryDate().compareTo(otherEntry.getEntryDate());
    }

    /*Returns all information in the order of: name, restaurant, tags */
    public ArrayList<String> getKeywords()
    {
        ArrayList<String> returnList = new ArrayList<String>();
        returnList.add(nameOfDish);
        returnList.add(restaurantName);
        for (String tag: tags)
        {
            returnList.add(tag);
        }
        return returnList;
    }

    //These methods will used to change the information in each datapoint.
    //They will be used both for the first time entering and for editing existing datapoints.
    public void incrementFrequency()
    {
        frequency += 1;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void rateDish(int newRating)
    {
        if(newRating < 0)
        {
            rating = 0;
        }
        else if(newRating > 10)
        {
            rating = 10;
        }
        else
        {
            rating = newRating;
        }
    }

    public Bitmap getThumbnail() { return thumbnail; };

    public int getRating()
    {
        return rating;
    }

    public String getRatingAsStars(){
        StringBuilder stars = new StringBuilder();
        if (getRating() % 2 == 0){
            for (int i = 0; i < getRating(); i+=2) {
                stars.append("★");
            }
            return stars.toString();
        } else {
            for (int i = 0; i < getRating()-1; i+=2) {
                stars.append("★");
            }
            stars.append("½");
            return stars.toString();
        }
    }

    public void setNameOfDish(String newName)
    {
         nameOfDish = newName;
    }

    public String getNameOfDish()
    {
        return nameOfDish;
    }

    public void setDescription(String newDescription)
    {
        description = newDescription;
    }

    public String getDescription()
    {
        return description;
    }

    public void setTags(List<String> newTags) {
        tags = (ArrayList)newTags;
    }

    //This one is for changing the name of the restaurant. Ideally, since Google will determine the restaurant, this will never be used.
    //Giving users the option is important, however.
    public void setRestaurantName(String newName)
    {
        restaurantName = newName;
    }

    public String getRestaurantName()
    {
        return restaurantName;
    }

    public ArrayList<String> getTags() { return tags; }

    public void newTag(String newTag)
    {
        tags.add(newTag);
    }

    public void removeTag(int tagToDrop)
    {
        if(tagToDrop >= 0 && tagToDrop < tags.size())
        {
            tags.remove(tagToDrop);
        }
    }

    public String GoogleFindRestaurantName()
    {
        //Whatever API magic needs to happen in here
        return null;
    }

    public ArrayList<String> GoogleFindTags()
    {
        //Whatever API magic needs to happen in here
        return null;
    }

    public int getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(int identify)
    {
        identifier = identify;
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date datedatedate)
    {
        entryDate = datedatedate;
    }

    public String getPhotoPath() { return photoPath; }

    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
