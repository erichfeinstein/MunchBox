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

/**
 * Created by Paul and Danny on 2/22/2018.
 */

public class JournalEntry
{
  private int identifier; //system-assigned
    private String nameOfDish;
    private String restaurantName;
    private int rating;
    public File photo = null;
    public int photoID;
    private int frequency;
    private String description;
    private ArrayList<String> tags = new ArrayList<String>();
    private Date entryDate = null;
    private Bitmap thumbnail = null;
    private String photoPath;



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

    public JournalEntry(Bitmap thumb, String path)
    {
        rating = 0;
        nameOfDish = "";
        restaurantName = "";
        //restaurantName = GoogleFindRestaurantName();
        //Generate tags
        PhotoAnalyzer labelGen = new PhotoAnalyzer(path);
        labelGen.generateLabels();
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
        //tags.add(GoogleFindTags);
        description = "";
        this.photoID = R.drawable.sample_image;
        frequency = 1;
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
        if (tags != null && tags.size() > 0) {
            nameOfDish = tags.get(0);
        }
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

    public void renameDish(String newName)
    {
         nameOfDish = newName;
    }

    public String getNameOfDish()
    {
        return nameOfDish;
    }

    public void newDescription(String newDescription)
    {
        description = newDescription;
    }

    public String getDescription()
    {
        return description;
    }

    //This one is for changing the name of the restaurant. Ideally, since Google will determine the restaurant, this will never be used.
    //Giving users the option is important, however.
    public void changeRestaurantName(String newName)
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

    public void setPhotoID(int ID)
    {
        photoID = ID;
    }

    public int getPhotoID()
    {
        return photoID;
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date datedatedate)
    {
        entryDate = datedatedate;
    }


}
