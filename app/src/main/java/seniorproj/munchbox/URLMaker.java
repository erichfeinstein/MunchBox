package seniorproj.munchbox;

import android.content.Context;
import android.location.Location;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class URLMaker {

    public static URL distanceMatrixURL(Context context, Location start, ArrayList<Location> end){
        String api_key = context.getString(R.string.mykey);
        try {
            String origin = start.getLatitude() + "%2C" + start.getLongitude();
            Location destination = end.remove(0);
            String destinations = "";
            destinations = destinations.concat(destination.getLatitude() + "%2C" + destination.getLongitude());
            while(!end.isEmpty()){
                destinations = destinations.concat("|");
                destination = end.remove(0);
                destinations = destinations.concat(destination.getLatitude() + "%2C" + destination.getLongitude());
            }
            String url_string = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origin + "&destinations=" + destinations + "&key=" + api_key;
            return new URL(url_string);
        }
        catch (MalformedURLException e){
            System.out.println(e.toString());
        }
        return null;
    }

    public static URL placesURL(Context context, Location location){
        String api_key = context.getString(R.string.mykey);
        try{
            String current_location = location.getLatitude() + "%2C" + location.getLongitude();
            String url_string = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + current_location + "&rankby=distance&keyword=restaurant&key=" + api_key;
            return new URL(url_string);
        }
        catch (MalformedURLException e){
            System.out.println(e.toString());
        }
        return null;
    }

}
