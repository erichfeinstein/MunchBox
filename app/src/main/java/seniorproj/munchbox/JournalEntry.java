package seniorproj.munchbox;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private String description;
    private ArrayList<String> tags = new ArrayList<>();
    private Date entryDate = null;
    private Bitmap thumbnail = null;
    private String photoPath;
    private int distanceMeters;
    private String distance;
    private double latitude = 0;
    private double longitude = 0;
    private boolean favorite;

    public JournalEntry(Bitmap thumb, String path)
    {
        rating = 0;
        nameOfDish = "";
        restaurantName = "";
        favorite = false;
        description = "";
        thumbnail = thumb;
        photoPath = path;
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
    }

    public JournalEntry() {
        rating = 0;
        favorite = false;
        nameOfDish = "";
        restaurantName = "";
        description = "";
        entryDate = Calendar.getInstance().getTime();  //generate date on entry creation
        if (tags != null && tags.size() > 0) {
            nameOfDish = tags.get(0);
        }
    }

    /*Used for sorting in MainActivity
      Only for comparing date of entry creation. Also the default sort option */
    @Override
    public int compareTo(JournalEntry otherEntry) {
        return getEntryDate().compareTo(otherEntry.getEntryDate());
    }

    /*Returns all information in the order of: name, restaurant, tags */
    public ArrayList<String> getKeywords()
    {
        ArrayList<String> returnList = new ArrayList<>();
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

    public void setRating(int rating)
    {
        if(rating < 0)
        {
            this.rating = 0;
        }
        else if(rating > 10)
        {
            this.rating = 10;
        }
        else
        {
            this.rating = rating;
        }
    }

    public Bitmap getThumbnail() { return thumbnail; }

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

    public void setDistanceMeters(int newDistance)
    {
        distanceMeters = newDistance;
    }

    public int getDistanceMeters()
    {
        return distanceMeters;
    }

    public void setDistance(String newDistance) {distance = newDistance;}

    public String getDistance(){return distance;}

    public boolean isFavorite()
    {
        return favorite;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location getLocation(){
        Location l = new Location(String.valueOf(getIdentifier()));
        l.setLongitude(longitude);
        l.setLatitude(latitude);
        return l;
    }
}
