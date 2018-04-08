package seniorproj.munchbox;

import android.content.Context;
import android.location.Location;

import java.net.MalformedURLException;
import java.net.URL;

public class URLMaker {

    public static URL distanceMatrixURL(Context context, Location start, Location end){
        String api_key = context.getString(R.string.mykey);
        try {
            String origin = start.getLatitude() + "%2C" + start.getLongitude();
            String destination = end.getLatitude() + "%2C" + end.getLongitude();
            String url_string = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + origin + "&destinations=" + destination + "&key=" + api_key;
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
